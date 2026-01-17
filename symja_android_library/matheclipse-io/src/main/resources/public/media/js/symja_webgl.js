/**
 * symja_webgl.js
 * * Renders Symja Graphics3D JSON data using Three.js.
 * Requires: three.module.js and OrbitControls.js to be loaded and exposed as window.THREE.
 */

(function(global) {

    // --- Helper: Material Generation ---
    function getMat(el, isLine) {
        var THREE = global.THREE;
        var params = {
            color: el.color,
            transparent: el.opacity < 1.0,
            opacity: el.opacity,
            side: THREE.DoubleSide,
            depthWrite: el.opacity >= 1.0,
            vertexColors: (el.vertexColors && el.vertexColors.length > 0) ? true : false
        };
        if (isLine) {
            if (el.dashed) return new THREE.LineDashedMaterial(Object.assign(params, { dashSize: 0.5, gapSize: 0.3, scale: 1 }));
            return new THREE.LineBasicMaterial(Object.assign(params, { linewidth: 2 }));
        }
        var mat = new THREE.MeshPhongMaterial(Object.assign(params, { shininess: 20, specular: 0x111111, flatShading: false }));
        if (el.type === 'Polygon') {
            mat.polygonOffset = true;
            mat.polygonOffsetFactor = 1;
            mat.polygonOffsetUnits = 1;
        }
        return mat;
    }

    // --- Helper: B-Spline Evaluation ---
    function evaluateBSpline(t, degree, points, knots, weights) {
        var THREE = global.THREE;
        var d = degree; var n = points.length / 3; var k = -1;
        for (var i = d;i < knots.length - 1 - d;i++) { if (t >= knots[i] && t <= knots[i + 1]) { k = i; break; } }
        if (k === -1) k = knots.length - 1 - d - 1;
        var v = [];
        for (var i = k - d;i <= k;i++) {
            var idx = i * 3; var w = (weights ? weights[i] : 1.0);
            v.push([points[idx] * w, points[idx + 1] * w, points[idx + 2] * w, w]);
        }
        for (var r = 1;r <= d;r++) {
            for (var j = d;j >= r;j--) {
                var alpha = (t - knots[j + k - d]) / (knots[j + 1 + k - r] - knots[j + k - d]);
                for (var x = 0;x < 4;x++) v[j][x] = (1.0 - alpha) * v[j - 1][x] + alpha * v[j][x];
            }
        }
        var res = v[d]; var rw = res[3];
        return new THREE.Vector3(res[0] / rw, res[1] / rw, res[2] / rw);
    }

    // --- Helper: Axis Drawing with Ticks ---
    function drawAxes(visualBox, dataBox, axes, scene, scalingTypes) {
        var THREE = global.THREE;
        var vMin = visualBox.min; var vMax = visualBox.max; var vSize = vMax.clone().sub(vMin);
        var dMin = dataBox.min; var dMax = dataBox.max; var dSize = dMax.clone().sub(dMin);

        var createLabel = function(text, pos) {
            var canvas = document.createElement('canvas'); var ctx = canvas.getContext('2d');
            // High-DPI scaling
            var scaleFactor = 2;
            ctx.font = (24 * scaleFactor) + 'px Arial';
            var width = ctx.measureText(text).width;
            canvas.width = width + 20; canvas.height = 64;

            ctx.font = (24 * scaleFactor) + 'px Arial';
            ctx.fillStyle = 'black';
            ctx.fillText(text, 10, 48);

            var tex = new THREE.CanvasTexture(canvas);
            // tex.minFilter = THREE.LinearFilter;
            var mat = new THREE.SpriteMaterial({ map: tex });
            var sprite = new THREE.Sprite(mat);
            sprite.position.copy(pos);

            // Scale sprite based on scene size
            var visualScale = 0.05 * Math.max(vSize.x, Math.max(vSize.y, vSize.z));
            sprite.scale.set(visualScale * canvas.width / canvas.height, visualScale, 1);
            return sprite;
        };

        var drawAxis = function(vStart, vEnd, axisIdx) {
            var color = 0x000000;
            // Axis Line
            scene.add(new THREE.Line(new THREE.BufferGeometry().setFromPoints([vStart, vEnd]), new THREE.LineBasicMaterial({ color: color })));

            var dataRange = (axisIdx === 0 ? dSize.x : (axisIdx === 1 ? dSize.y : dSize.z));
            var dataStart = (axisIdx === 0 ? dMin.x : (axisIdx === 1 ? dMin.y : dMin.z));

            if (dataRange <= 0) return;

            // Calculate nice step
            var step = Math.pow(10, Math.floor(Math.log10(dataRange)));
            if (dataRange / step < 2) step /= 5;
            else if (dataRange / step < 5) step /= 2;

            var visualDir = vEnd.clone().sub(vStart).normalize();
            var visualLen = vEnd.clone().sub(vStart).length();
            var tickSize = Math.max(vSize.x, Math.max(vSize.y, vSize.z)) * 0.02;
            var tickDir = new THREE.Vector3(0, 0, 0);
            if (axisIdx === 0) tickDir.set(0, -1, 0); else if (axisIdx === 1) tickDir.set(-1, 0, 0); else tickDir.set(-1, 0, 0);

            var firstTick = Math.ceil(dataStart / step) * step;
            if (firstTick < dataStart - 1e-9) firstTick += step; // Handle float precision

            for (var v = firstTick;v <= dataStart + dataRange + step / 1000;v += step) {
                // Map data value v to visual position ratio
                var ratio = (v - dataStart) / dataRange;
                var p = vStart.clone().add(visualDir.clone().multiplyScalar(ratio * visualLen));

                // Tick mark
                scene.add(new THREE.Line(new THREE.BufferGeometry().setFromPoints([p, p.clone().add(tickDir.clone().multiplyScalar(tickSize))]), new THREE.LineBasicMaterial({ color: color })));

                // Grid lines (optional, faint)
                // ...

                // Label
                var labelVal = v;
                if (scalingTypes && scalingTypes[axisIdx] === 'Log') labelVal = Math.pow(10, v);

                // Clean float format
                var labelText = Math.abs(labelVal) < 1e-6 ? "0" : labelVal.toPrecision(4).replace(/\.?0+$/, '');
                if (Math.abs(labelVal) >= 1000 || (Math.abs(labelVal) < 0.01 && labelVal !== 0)) labelText = labelVal.toExponential(2);

                var labelPos = p.clone().add(tickDir.clone().multiplyScalar(tickSize * 3.5));
                scene.add(createLabel(labelText, labelPos));
            }
        };

        if (axes[0]) drawAxis(vMin.clone(), vMin.clone().add(new THREE.Vector3(vSize.x, 0, 0)), 0);
        if (axes[1]) drawAxis(vMin.clone(), vMin.clone().add(new THREE.Vector3(0, vSize.y, 0)), 1);
        if (axes[2]) drawAxis(vMin.clone(), vMin.clone().add(new THREE.Vector3(0, 0, vSize.z)), 2);
    }

    // --- Main Rendering Function ---
    function renderSymjaWebGL(containerId, data) {
        var THREE = global.THREE;
        if (!THREE) { console.error("Three.js not found in global scope (window.THREE)"); return; }

        var container = document.getElementById(containerId);
        if (!container) { console.error("Container not found: " + containerId); return; }

        var width = container.clientWidth || 500; var height = container.clientHeight || 400;
        var scene = new THREE.Scene(); scene.background = new THREE.Color(0xffffff);

        var camera = new THREE.PerspectiveCamera(45, width / height, 0.1, 10000);
        camera.up.set(0, 0, 1); camera.position.set(10, -10, 10);
        scene.add(camera);

        var renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
        renderer.setSize(width, height);
        renderer.shadowMap.enabled = true;

        // Clear previous canvas if any
        while (container.firstChild) container.removeChild(container.firstChild);
        container.appendChild(renderer.domElement);

        var controls = new THREE.OrbitControls(camera, renderer.domElement);
        controls.enableDamping = true;

        // Lights
        if (data.lights) {
            data.lights.forEach(function(l) {
                var light;
                if (l.type === 'AmbientLight') light = new THREE.AmbientLight(l.color, l.intensity);
                else if (l.type === 'HemisphereLight') light = new THREE.HemisphereLight(l.color, 0x444444, 1.0);
                else if (l.type === 'DirectionalLight') {
                    light = new THREE.DirectionalLight(l.color, l.intensity);
                    if (l.position) light.position.set(l.position[0], l.position[1], l.position[2]);
                    light.castShadow = true;
                } else if (l.type === 'PointLight') {
                    light = new THREE.PointLight(l.color, l.intensity, l.distance || 0, l.decay || 1);
                    if (l.position) light.position.set(l.position[0], l.position[1], l.position[2]);
                } else if (l.type === 'SpotLight') {
                    light = new THREE.SpotLight(l.color, l.intensity, l.distance || 0, l.angle || Math.PI / 3, 0.1, l.decay || 1);
                    if (l.position) light.position.set(l.position[0], l.position[1], l.position[2]);
                    if (l.target) { var t = new THREE.Object3D(); t.position.set(l.target[0], l.target[1], l.target[2]); scene.add(t); light.target = t; }
                    light.castShadow = true;
                }
                if (light) { if (l.fixedToCamera) camera.add(light); else scene.add(light); }
            });
        }

        var objectsGroup = new THREE.Group();

        // --- Geometry Primitives ---
        if (data.elements) {
            data.elements.forEach(function(el) {
                var mesh;
                if (el.type === 'Polygon') { var geom = new THREE.BufferGeometry(); geom.setAttribute('position', new THREE.Float32BufferAttribute(el.points, 3)); if (el.vertexColors) geom.setAttribute('color', new THREE.Float32BufferAttribute(el.vertexColors, 3)); geom.computeVertexNormals(); mesh = new THREE.Mesh(geom, getMat(el, false)); }
                else if (el.type === 'Sphere') { mesh = new THREE.Mesh(new THREE.SphereGeometry(el.radius, 32, 32), getMat(el, false)); mesh.position.set(el.center[0], el.center[1], el.center[2]); }
                else if (el.type === 'Cone') { var s = new THREE.Vector3(el.start[0], el.start[1], el.start[2]); var e = new THREE.Vector3(el.end[0], el.end[1], el.end[2]); var h = s.distanceTo(e); var geom = new THREE.ConeGeometry(el.radius, h, 32); geom.translate(0, h / 2, 0); mesh = new THREE.Mesh(geom, getMat(el, false)); mesh.position.copy(s); mesh.quaternion.setFromUnitVectors(new THREE.Vector3(0, 1, 0), e.clone().sub(s).normalize()); }
                else if (el.type === 'Cylinder') { var s = new THREE.Vector3(el.start[0], el.start[1], el.start[2]); var e = new THREE.Vector3(el.end[0], el.end[1], el.end[2]); var h = s.distanceTo(e); var geom = new THREE.CylinderGeometry(el.radius, el.radius, h, 32); geom.translate(0, h / 2, 0); mesh = new THREE.Mesh(geom, getMat(el, false)); mesh.position.copy(s); mesh.quaternion.setFromUnitVectors(new THREE.Vector3(0, 1, 0), e.clone().sub(s).normalize()); }
                else if (el.type === 'Cuboid') { var w = el.max[0] - el.min[0]; var h = el.max[1] - el.min[1]; var d = el.max[2] - el.min[2]; mesh = new THREE.Mesh(new THREE.BoxGeometry(w, h, d), getMat(el, false)); mesh.position.set(el.min[0] + w / 2, el.min[1] + h / 2, el.min[2] + d / 2); }
                else if (el.type === 'Line') { var pts = []; for (var i = 0;i < el.points.length;i += 3) pts.push(new THREE.Vector3(el.points[i], el.points[i + 1], el.points[i + 2])); var geom = new THREE.BufferGeometry().setFromPoints(pts); mesh = new THREE.Line(geom, getMat(el, true)); if (el.dashed) mesh.computeLineDistances(); }
                else if (el.type === 'Tube') {
                    // requires BSplinePath definition or Catmull
                    var path;
                    if (el.pathType === 'BSpline') {
                        // Define BSplinePath class if not exists or use generic Curve
                        // Simplified for this context:
                        // path = new BSplinePath(el.degree, el.points, el.knots, el.weights); 
                    } else {
                        var pts = [];
                        for (var i = 0;i < el.points.length;i += 3) pts.push(new THREE.Vector3(el.points[i], el.points[i + 1], el.points[i + 2]));
                        if (pts.length > 1) path = new THREE.CatmullRomCurve3(pts);
                    }
                    if (path) { var geom = new THREE.TubeGeometry(path, 64, el.radius, 8, el.closed || false); mesh = new THREE.Mesh(geom, getMat(el, false)); }
                }
                else if (el.type === 'Point') { for (var i = 0;i < el.points.length;i += 3) { var m = new THREE.Mesh(new THREE.SphereGeometry(0.08, 8, 8), getMat(el, false)); m.position.set(el.points[i], el.points[i + 1], el.points[i + 2]); objectsGroup.add(m); } mesh = null; }
                if (mesh) { if (el.opacity >= 1.0) mesh.castShadow = true; mesh.receiveShadow = true; objectsGroup.add(mesh); }
            });
        }

        scene.add(objectsGroup);

        // --- Bounding Box & Scaling ---
        // 1. Get Data Bounds (Unscaled)
        var dataBox = new THREE.Box3().setFromObject(objectsGroup);
        if (dataBox.isEmpty()) {
            // Handle empty plot
            return;
        }

        // 2. Apply BoxRatios Scaling
        if (data.boxRatios && data.boxRatios.length === 3) {
            var size = dataBox.getSize(new THREE.Vector3());
            var br = data.boxRatios;
            var sx = size.x > 1e-9 ? br[0] / size.x : 1;
            var sy = size.y > 1e-9 ? br[1] / size.y : 1;
            var sz = size.z > 1e-9 ? br[2] / size.z : 1;

            // Normalize so max scale is 1 (to keep it reasonable in camera view)
            var maxScale = Math.max(sx, Math.max(sy, sz));
            if (maxScale > 0) {
                objectsGroup.scale.set(sx / maxScale, sy / maxScale, sz / maxScale);
                objectsGroup.updateMatrixWorld(true);
            }
        }

        // 3. Get Visual Bounds (Scaled)
        var visualBox = new THREE.Box3().setFromObject(objectsGroup);

        // --- Camera & Helpers ---
        if (!visualBox.isEmpty()) {
            var center = visualBox.getCenter(new THREE.Vector3());
            var size = visualBox.getSize(new THREE.Vector3());
            var maxDim = Math.max(size.x, Math.max(size.y, size.z));

            var fov = camera.fov * (Math.PI / 180);
            var cameraDist = maxDim / (2 * Math.tan(fov / 2));
            camera.position.copy(center).add(new THREE.Vector3(1, -1, 1).normalize().multiplyScalar(cameraDist * 2.0));
            camera.lookAt(center);
            controls.target.copy(center);

            // Bounding Box Helper (Wireframe)
            var helper = new THREE.Box3Helper(visualBox, 0x888888);
            scene.add(helper);

            // Axes with Ticks
            if (data.axes) {
                drawAxes(visualBox, dataBox, data.axes, scene, data.scaling);
            }

            // Legend
            if (data.showLegend && data.legendText) {
                var legendDiv = document.createElement('div');
                legendDiv.style.position = 'absolute';
                legendDiv.style.top = '10px';
                legendDiv.style.right = '10px';
                legendDiv.style.backgroundColor = 'rgba(255, 255, 255, 0.8)';
                legendDiv.style.padding = '5px';
                legendDiv.style.border = '1px solid #ccc';
                legendDiv.style.zIndex = '100';
                legendDiv.innerText = data.legendText;
                // Container must be relative for absolute child
                if (window.getComputedStyle(container).position === 'static') {
                    container.style.position = 'relative';
                }
                container.appendChild(legendDiv);
            }
        }

        function animate() { requestAnimationFrame(animate); controls.update(); renderer.render(scene, camera); }
        animate();

        window.addEventListener('resize', function() {
            if (container.clientWidth && container.clientHeight) {
                camera.aspect = container.clientWidth / container.clientHeight;
                camera.updateProjectionMatrix();
                renderer.setSize(container.clientWidth, container.clientHeight);
            }
        });
    }

    // Expose to global scope
    global.renderSymjaWebGL = renderSymjaWebGL;

})(window);