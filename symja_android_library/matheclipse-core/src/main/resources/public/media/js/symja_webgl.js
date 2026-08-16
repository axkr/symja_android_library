/**
 * symja_webgl.js
 *
 * Renders the JSON scene description that org.matheclipse.core.graphics.WebGLGraphics3D produces,
 * using Three.js. Requires three.module.js and OrbitControls.js to have been loaded and exposed as
 * window.THREE.
 *
 * The converter decides everything that depends on Wolfram semantics: the visible range, where the
 * ticks go and what they read, which lights are installed. This file turns that into geometry and
 * keeps the parts that can only be known once the scene is on screen - how big a pixel is, which
 * box edges face the viewer - up to date as the camera moves.
 */
(function (global) {

    'use strict';

    /** One printer's point, as a fraction of the scene diagonal. Tuned so Thick reads as thick. */
    var POINT_TO_WORLD = 0.004;

    /**
     * Below this radius a line is drawn as a plain one pixel line rather than swept into a tube.
     * Set so that the default width stays a hairline and Thick becomes a tube.
     */
    var MIN_TUBE_RADIUS = 0.0025;

    /**
     * Light intensities arrive in Wolfram's units, where a light of colour c contributes c times
     * the cosine of the incidence angle. Three.js divides the diffuse term by pi, as a physically
     * normalised Lambert lobe does, so an intensity of pi is what reproduces the value Wolfram
     * would have drawn. Without it every default lit surface comes out at about a third of its
     * brightness, which reads as a muddy brown rather than the warm gold it should be.
     */
    var WOLFRAM_LIGHT_INTENSITY = Math.PI;

    // ------------------------------------------------------------------ maths

    function vec(array, offset) {
        var THREE = global.THREE;
        return new THREE.Vector3(array[offset], array[offset + 1], array[offset + 2]);
    }

    /** Resolve a size that the converter wrote either scaled or in points. */
    function resolveSize(el, name, diagonal, fallbackPoints) {
        var scaled = el[name + 'Scaled'];
        if (typeof scaled === 'number') {
            return scaled * diagonal;
        }
        var absolute = el[name];
        if (typeof absolute === 'number') {
            return absolute * diagonal * POINT_TO_WORLD;
        }
        return (fallbackPoints || 1) * diagonal * POINT_TO_WORLD;
    }

    function evaluateBSpline(t, degree, points, knots, weights) {
        var THREE = global.THREE;
        var d = degree, n = points.length / 3, k = -1;
        for (var i = d; i < knots.length - 1 - d; i++) {
            if (t >= knots[i] && t <= knots[i + 1]) { k = i; break; }
        }
        if (k === -1) k = knots.length - 2 - d;
        var v = [];
        for (var j = k - d; j <= k; j++) {
            var idx = j * 3;
            var w = weights ? weights[j] : 1.0;
            v.push([points[idx] * w, points[idx + 1] * w, points[idx + 2] * w, w]);
        }
        for (var r = 1; r <= d; r++) {
            for (var s = d; s >= r; s--) {
                var denom = knots[s + 1 + k - r] - knots[s + k - d];
                var alpha = denom === 0 ? 0 : (t - knots[s + k - d]) / denom;
                for (var x = 0; x < 4; x++) {
                    v[s][x] = (1.0 - alpha) * v[s - 1][x] + alpha * v[s][x];
                }
            }
        }
        var res = v[d];
        var rw = res[3] || 1;
        return new THREE.Vector3(res[0] / rw, res[1] / rw, res[2] / rw);
    }

    /**
     * The B-spline curve class, built the first time one is needed.
     *
     * <p>Three.js exports ES6 classes, which cannot be subclassed with the prototype idiom and
     * cannot be constructed without `new`, so the subclass has to be declared with `class` - and
     * it can only be declared once THREE is on the page.
     */
    var BSplineCurveClass = null;

    function bSplineCurveClass() {
        var THREE = global.THREE;
        if (BSplineCurveClass === null) {
            BSplineCurveClass = class extends THREE.Curve {
                constructor(degree, points, knots, weights) {
                    super();
                    this.degree = Math.max(1, degree || 3);
                    this.points = points || [];
                    this.knots = knots || [];
                    this.weights = weights;
                }
                getPoint(t, target) {
                    var d = this.degree;
                    var start = this.knots[d];
                    var end = this.knots[this.knots.length - 1 - d];
                    var u = start + t * (end - start);
                    var p = evaluateBSpline(u, d, this.points, this.knots, this.weights);
                    return target ? target.copy(p) : p;
                }
            };
        }
        return BSplineCurveClass;
    }

    function makeBSplineCurve(el) {
        var Class = bSplineCurveClass();
        return new Class(el.degree, el.points, el.knots, el.weights);
    }

    // -------------------------------------------------------------- materials

    /**
     * The primitives that enclose a volume, whose inside is never meant to be seen.
     *
     * <p>A tube is not among them: its ends are open, and culling its inside would show a hollow
     * gap to anyone looking down its axis.
     */
    var CLOSED_SOLIDS = {
        Sphere: true, Cylinder: true, Cone: true, Cuboid: true, Polyhedron: true
    };

    function surfaceMaterial(el) {
        var THREE = global.THREE;
        // A mathematical surface is a sheet and has to be lit from both sides, but a box or a
        // sphere is closed: drawing its interior as well means a translucent one is blended twice
        // over, which turns a half transparent bar into a muddy solid.
        var closed = CLOSED_SOLIDS[el.type] === true;
        var params = {
            color: el.color,
            transparent: el.opacity < 1.0,
            opacity: typeof el.opacity === 'number' ? el.opacity : 1.0,
            side: closed ? THREE.FrontSide : THREE.DoubleSide,
            depthWrite: el.opacity >= 1.0,
            vertexColors: !!(el.vertexColors && el.vertexColors.length > 0),
            shininess: typeof el.specularExponent === 'number' ? el.specularExponent : 20,
            flatShading: false
        };
        var material = new THREE.MeshPhongMaterial(params);
        if (typeof el.specularity === 'number') {
            var s = Math.round(Math.max(0, Math.min(1, el.specularity)) * 255);
            material.specular = new THREE.Color((s << 16) | (s << 8) | s);
        } else {
            material.specular = new THREE.Color(0x111111);
        }
        if (typeof el.glow === 'number') {
            material.emissive = new THREE.Color(el.glow);
        }
        if (el.type === 'Polygon') {
            // keeps the mesh overlay from z-fighting with the surface it outlines
            material.polygonOffset = true;
            material.polygonOffsetFactor = 1;
            material.polygonOffsetUnits = 1;
        }
        return material;
    }

    function lineMaterial(el, diagonal) {
        var THREE = global.THREE;
        var params = {
            color: el.color,
            transparent: el.opacity < 1.0,
            opacity: typeof el.opacity === 'number' ? el.opacity : 1.0,
            vertexColors: !!(el.vertexColors && el.vertexColors.length > 0)
        };
        if (el.dashing && el.dashing.length >= 2) {
            var unit = el.dashingScaled === false ? diagonal * POINT_TO_WORLD : diagonal;
            params.dashSize = Math.max(1e-6, el.dashing[0] * unit);
            params.gapSize = Math.max(1e-6, el.dashing[1] * unit);
            return new THREE.LineDashedMaterial(params);
        }
        return new THREE.LineBasicMaterial(params);
    }

    // ------------------------------------------------------------- primitives

    function buildPolygon(el) {
        var THREE = global.THREE;
        var geometry = new THREE.BufferGeometry();
        geometry.setAttribute('position', new THREE.Float32BufferAttribute(el.points, 3));
        if (el.vertexColors && el.vertexColors.length > 0) {
            geometry.setAttribute('color', new THREE.Float32BufferAttribute(el.vertexColors, 3));
        }
        if (el.indices) {
            geometry.setIndex(el.indices);
        }
        if (el.vertexNormals && el.vertexNormals.length === el.points.length) {
            // the converter supplies analytic normals for surfaces that have them, which shade
            // far better than normals averaged from the triangles
            geometry.setAttribute('normal', new THREE.Float32BufferAttribute(el.vertexNormals, 3));
        } else {
            geometry.computeVertexNormals();
        }
        var material = surfaceMaterial(el);
        var mesh;
        if (typeof el.backColor === 'number') {
            // FaceForm[front, back] gives the two sides of a surface their own colour. One
            // material can only carry one, so the far side is drawn as a second mesh over the
            // same geometry, each material limited to the side it is meant for.
            material.side = THREE.FrontSide;
            mesh = new THREE.Mesh(geometry, material);
            var backMaterial = surfaceMaterial(el);
            backMaterial.color = new THREE.Color(el.backColor);
            backMaterial.side = THREE.BackSide;
            mesh.add(new THREE.Mesh(geometry, backMaterial));
        } else {
            mesh = new THREE.Mesh(geometry, material);
        }
        if (el.showMesh) {
            var edges = new THREE.EdgesGeometry(geometry, 1);
            var color = typeof el.edgeColor === 'number' ? el.edgeColor : 0x333333;
            var opacity = typeof el.edgeOpacity === 'number' ? el.edgeOpacity : 0.6;
            mesh.add(new THREE.LineSegments(edges, new THREE.LineBasicMaterial({
                color: color, transparent: opacity < 1.0, opacity: opacity
            })));
        }
        return mesh;
    }

    /**
     * A polyline. WebGL cannot widen a line primitive, so anything the user asked to be thicker
     * than a hairline is swept into a tube instead; that is also what makes it catch the light the
     * way the surfaces around it do.
     */
    function buildPolylines(el, diagonal, group) {
        var THREE = global.THREE;
        var polylines = el.polylines || (el.points ? [el.points] : []);
        var radius = resolveSize(el, 'thickness', diagonal, 1) * 0.5;
        var asTube = radius >= MIN_TUBE_RADIUS * diagonal && !el.dashing;

        for (var i = 0; i < polylines.length; i++) {
            var flat = polylines[i];
            if (!flat || flat.length < 6) { continue; }
            var points = [];
            for (var j = 0; j < flat.length; j += 3) {
                points.push(new THREE.Vector3(flat[j], flat[j + 1], flat[j + 2]));
            }
            if (asTube) {
                var path = new THREE.CatmullRomCurve3(points, false, 'catmullrom', 0.0);
                var segments = Math.min(600, Math.max(points.length * 2, 8));
                var tube = new THREE.TubeGeometry(path, segments, radius, 8, false);
                group.add(new THREE.Mesh(tube, surfaceMaterial(el)));
            } else {
                var geometry = new THREE.BufferGeometry().setFromPoints(points);
                var line = new THREE.Line(geometry, lineMaterial(el, diagonal));
                if (el.dashing) { line.computeLineDistances(); }
                group.add(line);
            }
        }
    }

    /**
     * Point markers.
     *
     * <p>A point is a round dot of a given size, and stays one however the box is proportioned, so
     * these are built in the unscaled frame with their positions carried across by hand. Leaving
     * them inside the group that BoxRatios scales stretches each dot by the same factor as the
     * axis: on a plot whose values span a hundredth of its domain, that turns every marker into a
     * streak running far outside the box.
     */
    function buildPoints(el, diagonal, group, scaleVector) {
        var THREE = global.THREE;
        var flat = el.points || [];
        var count = Math.floor(flat.length / 3);
        if (count === 0) { return; }
        var radius = resolveSize(el, 'pointSize', diagonal, 3) * 0.5;
        var geometry = new THREE.SphereGeometry(radius, 12, 8);
        var material = surfaceMaterial(el);
        material.vertexColors = false;
        var mesh = new THREE.InstancedMesh(geometry, material, count);
        var matrix = new THREE.Matrix4();
        var position = new THREE.Vector3();
        var own = el.matrix && el.matrix.length === 16
            ? new THREE.Matrix4().fromArray(el.matrix) : null;
        var color = new THREE.Color();
        for (var i = 0; i < count; i++) {
            position.set(flat[i * 3], flat[i * 3 + 1], flat[i * 3 + 2]);
            if (own) { position.applyMatrix4(own); }
            position.multiply(scaleVector);
            matrix.makeTranslation(position.x, position.y, position.z);
            mesh.setMatrixAt(i, matrix);
            if (el.vertexColors && el.vertexColors.length >= (i + 1) * 3) {
                color.setRGB(el.vertexColors[i * 3], el.vertexColors[i * 3 + 1],
                    el.vertexColors[i * 3 + 2]);
                mesh.setColorAt(i, color);
            }
        }
        mesh.instanceMatrix.needsUpdate = true;
        if (mesh.instanceColor) { mesh.instanceColor.needsUpdate = true; }
        group.add(mesh);
    }

    function buildSpheres(el, group) {
        var THREE = global.THREE;
        var flat = el.centers || [];
        var count = Math.floor(flat.length / 3);
        if (count === 0) { return; }
        var geometry = new THREE.SphereGeometry(el.radius, 40, 30);
        var material = surfaceMaterial(el);
        if (count === 1) {
            var mesh = new THREE.Mesh(geometry, material);
            mesh.position.set(flat[0], flat[1], flat[2]);
            group.add(mesh);
            return;
        }
        var instanced = new THREE.InstancedMesh(geometry, material, count);
        var matrix = new THREE.Matrix4();
        for (var i = 0; i < count; i++) {
            matrix.makeTranslation(flat[i * 3], flat[i * 3 + 1], flat[i * 3 + 2]);
            instanced.setMatrixAt(i, matrix);
        }
        instanced.instanceMatrix.needsUpdate = true;
        group.add(instanced);
    }

    /** Orient a solid that is built along +Y onto the segment start..end. */
    function orientAlong(mesh, start, end) {
        var THREE = global.THREE;
        mesh.position.copy(start);
        var direction = end.clone().sub(start);
        if (direction.lengthSq() > 0) {
            mesh.quaternion.setFromUnitVectors(new THREE.Vector3(0, 1, 0), direction.normalize());
        }
    }

    function buildCylinder(el) {
        var THREE = global.THREE;
        var start = vec(el.start, 0), end = vec(el.end, 0);
        var height = start.distanceTo(end);
        var geometry = new THREE.CylinderGeometry(el.radius, el.radius, height, 40, 1);
        geometry.translate(0, height / 2, 0);
        var mesh = new THREE.Mesh(geometry, surfaceMaterial(el));
        orientAlong(mesh, start, end);
        return mesh;
    }

    function buildCone(el) {
        var THREE = global.THREE;
        var start = vec(el.start, 0), end = vec(el.end, 0);
        var height = start.distanceTo(end);
        var geometry = new THREE.ConeGeometry(el.radius, height, 40, 1);
        geometry.translate(0, height / 2, 0);
        var mesh = new THREE.Mesh(geometry, surfaceMaterial(el));
        orientAlong(mesh, start, end);
        return mesh;
    }

    function buildCuboid(el) {
        var THREE = global.THREE;
        var w = el.max[0] - el.min[0];
        var h = el.max[1] - el.min[1];
        var d = el.max[2] - el.min[2];
        var mesh = new THREE.Mesh(new THREE.BoxGeometry(w, h, d), surfaceMaterial(el));
        mesh.position.set(el.min[0] + w / 2, el.min[1] + h / 2, el.min[2] + d / 2);
        return mesh;
    }

    function buildPolyhedron(el) {
        var THREE = global.THREE;
        var r = el.scale || 1;
        var geometry;
        switch (el.kind) {
            case 'Tetrahedron': geometry = new THREE.TetrahedronGeometry(r); break;
            case 'Octahedron': geometry = new THREE.OctahedronGeometry(r); break;
            case 'Dodecahedron': geometry = new THREE.DodecahedronGeometry(r); break;
            case 'Icosahedron': geometry = new THREE.IcosahedronGeometry(r); break;
            default: geometry = new THREE.BoxGeometry(2 * r, 2 * r, 2 * r); break;
        }
        var mesh = new THREE.Mesh(geometry, surfaceMaterial(el));
        mesh.position.set(el.center[0], el.center[1], el.center[2]);
        return mesh;
    }

    function buildTube(el) {
        var THREE = global.THREE;
        var path = null;
        if (el.pathType === 'BSpline') {
            path = makeBSplineCurve(el);
        } else {
            var flat = (el.polylines && el.polylines[0]) || el.points || [];
            var points = [];
            for (var i = 0; i < flat.length; i += 3) {
                points.push(new THREE.Vector3(flat[i], flat[i + 1], flat[i + 2]));
            }
            if (points.length > 1) {
                path = new THREE.CatmullRomCurve3(points, !!el.closed);
            }
        }
        if (!path) { return null; }
        var geometry = new THREE.TubeGeometry(path, 128, el.radius, 16, !!el.closed);
        return new THREE.Mesh(geometry, surfaceMaterial(el));
    }

    function buildCurve(el, diagonal, group, curve) {
        var THREE = global.THREE;
        var points = curve.getPoints(128);
        var radius = resolveSize(el, 'thickness', diagonal, 1) * 0.5;
        if (radius >= MIN_TUBE_RADIUS * diagonal && !el.dashing) {
            var tube = new THREE.TubeGeometry(curve, 128, radius, 8, false);
            group.add(new THREE.Mesh(tube, surfaceMaterial(el)));
            return;
        }
        var geometry = new THREE.BufferGeometry().setFromPoints(points);
        var line = new THREE.Line(geometry, lineMaterial(el, diagonal));
        if (el.dashing) { line.computeLineDistances(); }
        group.add(line);
    }

    function buildBezier(el, diagonal, group) {
        var THREE = global.THREE;
        var flat = el.points || [];
        var points = [];
        for (var i = 0; i < flat.length; i += 3) {
            points.push(new THREE.Vector3(flat[i], flat[i + 1], flat[i + 2]));
        }
        if (points.length < 2) { return; }
        var curve;
        if (points.length === 3) {
            curve = new THREE.QuadraticBezierCurve3(points[0], points[1], points[2]);
        } else if (points.length >= 4) {
            curve = new THREE.CubicBezierCurve3(points[0], points[1], points[2], points[3]);
        } else {
            curve = new THREE.LineCurve3(points[0], points[1]);
        }
        buildCurve(el, diagonal, group, curve);
    }

    /** A line with a cone at the tip, sized as a fraction of the scene. */
    function buildArrow(el, diagonal, group) {
        var THREE = global.THREE;
        buildPolylines(el, diagonal, group);
        var polylines = el.polylines || [];
        var headSize = (typeof el.arrowheadSize === 'number' ? el.arrowheadSize : 0.04) * diagonal;
        if (headSize <= 0) { return; }
        for (var i = 0; i < polylines.length; i++) {
            var flat = polylines[i];
            if (!flat || flat.length < 6) { continue; }
            var tip = vec(flat, flat.length - 3);
            var previous = vec(flat, flat.length - 6);
            var direction = tip.clone().sub(previous);
            if (direction.lengthSq() === 0) { continue; }
            direction.normalize();
            var geometry = new THREE.ConeGeometry(headSize * 0.35, headSize, 20);
            geometry.translate(0, -headSize / 2, 0);
            var cone = new THREE.Mesh(geometry, surfaceMaterial(el));
            cone.position.copy(tip);
            cone.quaternion.setFromUnitVectors(new THREE.Vector3(0, 1, 0), direction);
            group.add(cone);
        }
    }

    // ------------------------------------------------------------------- text

    /**
     * A text sprite whose size stays constant on screen, which is how Wolfram draws labels in 3D.
     * The device pixel ratio is baked into the canvas so the glyphs stay sharp when zoomed.
     */
    function makeTextSprite(text, options) {
        var THREE = global.THREE;
        var fontSize = options.fontSize || 12;
        var ratio = Math.min(global.devicePixelRatio || 1, 3);
        var scale = 3 * ratio;
        var canvas = document.createElement('canvas');
        var ctx = canvas.getContext('2d');
        var font = (options.fontStyle || 'normal') + ' ' + (options.fontWeight || 'normal') + ' '
            + (fontSize * scale) + 'px ' + (options.fontFamily || 'Arial, sans-serif');
        // A tick on a log axis reads 10^n. It is set as a superscript rather than printed with
        // a caret, the same way the 2D axes write it.
        var power = /^10\^(-?\d+)$/.exec(text);
        var base = power ? '10' : text;
        var exponent = power ? power[1] : '';
        var superFont = (options.fontStyle || 'normal') + ' ' + (options.fontWeight || 'normal')
            + ' ' + Math.round(fontSize * scale * 0.7) + 'px '
            + (options.fontFamily || 'Arial, sans-serif');

        ctx.font = font;
        var baseWidth = Math.max(1, Math.ceil(ctx.measureText(base).width));
        var width = baseWidth;
        if (power) {
            ctx.font = superFont;
            width += Math.ceil(ctx.measureText(exponent).width);
        }
        canvas.width = width + 4 * scale;
        canvas.height = Math.ceil(fontSize * scale * (power ? 1.6 : 1.4));

        ctx.textBaseline = 'middle';
        ctx.fillStyle = options.color || '#000000';
        ctx.font = font;
        var baseline = power ? canvas.height * 0.62 : canvas.height / 2;
        ctx.fillText(base, 2 * scale, baseline);
        if (power) {
            ctx.font = superFont;
            ctx.fillText(exponent, 2 * scale + baseWidth, baseline - fontSize * scale * 0.35);
        }

        var texture = new THREE.CanvasTexture(canvas);
        texture.minFilter = THREE.LinearFilter;
        var material = new THREE.SpriteMaterial({
            map: texture, transparent: true, depthTest: options.depthTest !== false,
            sizeAttenuation: false
        });
        var sprite = new THREE.Sprite(material);
        sprite.userData.pixelWidth = canvas.width / scale;
        sprite.userData.pixelHeight = canvas.height / scale;
        return sprite;
    }

    /**
     * Size every constant-size sprite for the current viewport.
     *
     * <p>A sprite with sizeAttenuation off is sized in view units that three.js multiplies by the
     * view depth, so what reaches the screen is the scale times the projection's vertical factor,
     * over the two units of clip space that the viewport height covers. Skipping that factor made
     * every tick label about one and a half times too big, and by an amount that changed with the
     * field of view.
     */
    function updateSpriteScales(sprites, height, camera) {
        var projection = camera.projectionMatrix.elements[5];
        if (!(projection > 0)) { projection = 1; }
        for (var i = 0; i < sprites.length; i++) {
            var sprite = sprites[i];
            var h = 2 * (sprite.userData.pixelHeight / height) / projection;
            var w = h * sprite.userData.pixelWidth / sprite.userData.pixelHeight;
            sprite.scale.set(w, h, 1);
        }
    }

    function cssColor(value, fallback) {
        if (typeof value !== 'number') { return fallback; }
        return '#' + ('000000' + value.toString(16)).slice(-6);
    }

    // ------------------------------------------------------------------ scene

    /**
     * Show a graphic in a container.
     *
     * <p>A browser allows only a handful of WebGL contexts at once - well under the number of
     * graphics a document of worked examples contains - and quietly discards the oldest when the
     * limit is passed, which leaves earlier graphics blank. So a scene is built when it comes near
     * the viewport and torn down again when it leaves, and a page may then hold any number.
     */
    function renderSymjaWebGL(containerId, data) {
        if (!global.THREE) {
            // three.js is loaded as a module, which is deferred until the page has been parsed,
            // so a graphic in the page body can arrive first. Wait for the loader to drain us.
            global.SymjaWebGLQueue = global.SymjaWebGLQueue || [];
            global.SymjaWebGLQueue.push([containerId, data]);
            return;
        }
        var container = document.getElementById(containerId);
        if (!container) {
            console.error('symja_webgl: container not found: ' + containerId);
            return;
        }
        if (typeof IntersectionObserver !== 'function') {
            buildScene(container, data);
            return;
        }
        var live = null;
        var observer = new IntersectionObserver(function (entries) {
            for (var i = 0; i < entries.length; i++) {
                if (entries[i].isIntersecting) {
                    if (!live) {
                        try {
                            live = buildScene(container, data);
                        } catch (error) {
                            console.error('symja_webgl: ' + containerId + ': ' + error);
                            observer.disconnect();
                        }
                    }
                } else if (live) {
                    live.dispose();
                    live = null;
                }
            }
        }, { rootMargin: '300px 0px' });
        observer.observe(container);
    }

    /**
     * Lay a Prolog or Epilog picture over the canvas.
     *
     * The converter has already drawn it, with the ordinary two dimensional renderer and at the
     * size of this view, so there is nothing to interpret here: it is placed over the same box the
     * canvas occupies. Clicks pass through it, so it never gets in the way of turning the scene.
     */
    function addFlatOverlay(container, svg, width, height) {
        if (!svg) { return; }
        var layer = document.createElement('div');
        layer.style.cssText = 'position:absolute;left:0;top:0;pointer-events:none;'
            + 'width:' + width + 'px;height:' + height + 'px;';
        layer.innerHTML = svg;
        var picture = layer.firstElementChild;
        if (picture) {
            // The overlay is drawn in scaled coordinates, so it belongs to the whole view however
            // large that turns out to be. Left at the size it was drawn at it would sit in a
            // corner of a bigger canvas, which is where the label ended up.
            picture.removeAttribute('style');
            picture.setAttribute('width', '100%');
            picture.setAttribute('height', '100%');
            picture.setAttribute('preserveAspectRatio', 'none');
        }
        container.appendChild(layer);
    }

    /** Four insets from the scene as left, right, bottom, top, or zero on every side. */
    function insets(spec) {
        if (spec && spec.length >= 4) { return spec; }
        return [0, 0, 0, 0];
    }

    /** The six sides of the scene's box, as half spaces that keep what is inside it. */
    function boxPlanes(visualMin, visualMax) {
        var THREE = global.THREE;
        return [
            new THREE.Plane(new THREE.Vector3(1, 0, 0), -visualMin.x),
            new THREE.Plane(new THREE.Vector3(-1, 0, 0), visualMax.x),
            new THREE.Plane(new THREE.Vector3(0, 1, 0), -visualMin.y),
            new THREE.Plane(new THREE.Vector3(0, -1, 0), visualMax.y),
            new THREE.Plane(new THREE.Vector3(0, 0, 1), -visualMin.z),
            new THREE.Plane(new THREE.Vector3(0, 0, -1), visualMax.z)
        ];
    }

    /**
     * Draw the clipping planes themselves, which is what ClipPlanesStyle asks for.
     *
     * Each plane is a square big enough to cross the scene, laid in the plane and cut down by
     * the others, so what shows is the piece that fills the cut it made. Without a style nothing
     * is drawn, which is what the option defaults to.
     */
    function addClipPlaneSurfaces(parent, data, clipPlanes, scaleVector, visualMin, visualMax) {
        var THREE = global.THREE;
        if (!data.clipPlanesStyle) { return; }
        var size = new THREE.Vector3().subVectors(visualMax, visualMin);
        var reach = size.length();
        var centre = new THREE.Vector3().addVectors(visualMin, visualMax).multiplyScalar(0.5);
        for (var i = 0; i < clipPlanes.length && i < data.clipPlanesStyle.length; i++) {
            var style = data.clipPlanesStyle[i];
            var material = new THREE.MeshBasicMaterial({
                color: style.color,
                transparent: style.opacity < 1.0,
                opacity: typeof style.opacity === 'number' ? style.opacity : 1.0,
                side: THREE.DoubleSide,
                // cut by the other planes, but never by its own, and kept inside the box so it
                // fills the cut it made rather than sweeping across the whole view
                clippingPlanes: clipPlanes.filter(function (p, j) { return j !== i; })
                    .concat(boxPlanes(visualMin, visualMax))
            });
            var quad = new THREE.Mesh(new THREE.PlaneGeometry(reach, reach), material);
            var plane = clipPlanes[i];
            // face along the plane's normal, and sit where it passes closest to the scene
            quad.quaternion.setFromUnitVectors(new THREE.Vector3(0, 0, 1), plane.normal);
            quad.position.copy(plane.projectPoint(centre, new THREE.Vector3()));
            // the geometry is added to the scaled group, so undo that scaling for this square
            quad.scale.set(1 / scaleVector.x, 1 / scaleVector.y, 1 / scaleVector.z);
            parent.add(quad);
        }
    }

    function buildScene(container, data) {
        var THREE = global.THREE;
        var width = container.clientWidth || 360;
        var height = container.clientHeight || 360;

        // ImageMargins is room kept outside the picture, so it is left around the whole box;
        // ImagePadding is room kept inside it and PlotRegion narrows the drawing further. All
        // three are worked out the same way here as they are for the static picture, so the two
        // outputs frame the scene alike.
        var margins = insets(data.imageMargins);
        var padding = insets(data.imagePadding);
        if (data.imageMargins) {
            container.style.margin = margins[3] + 'px ' + margins[1] + 'px '
                + margins[2] + 'px ' + margins[0] + 'px';
        }
        var areaLeft = padding[0];
        var areaBottom = padding[2];
        var areaWidth = Math.max(1, width - padding[0] - padding[1]);
        var areaHeight = Math.max(1, height - padding[2] - padding[3]);
        if (data.plotRegion && data.plotRegion.length >= 2) {
            var rx = data.plotRegion[0];
            var ry = data.plotRegion[1];
            areaLeft += rx[0] * areaWidth;
            areaBottom += ry[0] * areaHeight;
            areaWidth = Math.max(1, (rx[1] - rx[0]) * areaWidth);
            areaHeight = Math.max(1, (ry[1] - ry[0]) * areaHeight);
        }
        var inset = areaWidth !== width || areaHeight !== height;
        if (inset && !container.style.position) { container.style.position = 'relative'; }
        width = areaWidth;
        height = areaHeight;

        var scene = new THREE.Scene();
        if (typeof data.background === 'number') {
            scene.background = new THREE.Color(data.background);
        } else {
            scene.background = new THREE.Color(0xffffff);
        }

        var renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
        renderer.setPixelRatio(Math.min(global.devicePixelRatio || 1, 2));
        renderer.setSize(width, height);
        while (container.firstChild) { container.removeChild(container.firstChild); }
        // the overlays are positioned against the container, so it has to be a positioned box
        if (!container.style.position) { container.style.position = 'relative'; }
        addFlatOverlay(container, data.prolog, width, height);
        if (inset) {
            renderer.domElement.style.position = 'absolute';
            renderer.domElement.style.left = areaLeft + 'px';
            // the drawing area is measured from the bottom, the page from the top
            renderer.domElement.style.bottom = areaBottom + 'px';
        }
        container.appendChild(renderer.domElement);
        addFlatOverlay(container, data.epilog, width, height);

        // --- the data range, and the box the scene is drawn in

        var ranges = data.plotRange || [[-1, 1], [-1, 1], [-1, 1]];
        var dataMin = new THREE.Vector3(ranges[0][0], ranges[1][0], ranges[2][0]);
        var dataMax = new THREE.Vector3(ranges[0][1], ranges[1][1], ranges[2][1]);
        var dataSize = dataMax.clone().sub(dataMin);

        // BoxRatios reshapes the box the data is drawn into; without it the data keeps its own
        // proportions, which is what Automatic means
        var ratios = data.boxRatios;
        var scaleVector = new THREE.Vector3(1, 1, 1);
        if (ratios && ratios.length === 3) {
            var longest = Math.max(dataSize.x, Math.max(dataSize.y, dataSize.z)) || 1;
            var maxRatio = Math.max(ratios[0], Math.max(ratios[1], ratios[2])) || 1;
            scaleVector.set(
                dataSize.x > 1e-12 ? (ratios[0] / maxRatio) * longest / dataSize.x : 1,
                dataSize.y > 1e-12 ? (ratios[1] / maxRatio) * longest / dataSize.y : 1,
                dataSize.z > 1e-12 ? (ratios[2] / maxRatio) * longest / dataSize.z : 1);
        }

        // ClipPlanes cuts the contents down to a half space each. The planes are given in the
        // data's own coordinates and the contents are drawn in a box that has been squared up, so
        // the normals follow the same scaling: scaling a point by s turns n.p + d into
        // (n/s).p' + d. They are applied to the contents alone, not through the renderer, so the
        // box and the axes around the scene stay whole.
        var clipPlanes = [];
        if (data.clipPlanes) {
            for (var ci = 0; ci < data.clipPlanes.length; ci++) {
                var spec = data.clipPlanes[ci];
                if (!spec || spec.length < 4) { continue; }
                var normal = new THREE.Vector3(spec[0] / scaleVector.x, spec[1] / scaleVector.y,
                    spec[2] / scaleVector.z);
                var length = normal.length();
                if (length < 1e-12) { continue; }
                clipPlanes.push(new THREE.Plane(normal.divideScalar(length), spec[3] / length));
            }
        }
        if (clipPlanes.length > 0) { renderer.localClippingEnabled = true; }

        var objects = new THREE.Group();
        objects.scale.copy(scaleVector);
        // markers keep their own shape whatever proportions the box is given, so they live
        // outside the scaled group and carry their positions across themselves
        var markers = new THREE.Group();

        var visualMin = dataMin.clone().multiply(scaleVector);
        var visualMax = dataMax.clone().multiply(scaleVector);
        var visualBox = new THREE.Box3(visualMin.clone().min(visualMax),
            visualMin.clone().max(visualMax));
        var center = visualBox.getCenter(new THREE.Vector3());
        var visualSize = visualBox.getSize(new THREE.Vector3());
        var maxDim = Math.max(visualSize.x, Math.max(visualSize.y, visualSize.z)) || 1;
        // sizes the converter wrote as fractions are fractions of this
        var diagonal = typeof data.diagonal === 'number' && data.diagonal > 0
            ? data.diagonal : visualSize.length() || 1;
        // a marker sits in the unscaled frame, so its size is measured against the box as drawn
        var visualDiagonal = visualSize.length() || diagonal;

        // --- geometry

        if (data.elements) {
            for (var i = 0; i < data.elements.length; i++) {
                var el = data.elements[i];
                var built = null;
                var group = new THREE.Group();
                switch (el.type) {
                    case 'Polygon': built = buildPolygon(el); break;
                    case 'Sphere': buildSpheres(el, group); built = group; break;
                    case 'Cylinder': built = buildCylinder(el); break;
                    case 'Cone': built = buildCone(el); break;
                    case 'Cuboid': built = buildCuboid(el); break;
                    case 'Polyhedron': built = buildPolyhedron(el); break;
                    case 'Tube': built = buildTube(el); break;
                    case 'Line': buildPolylines(el, diagonal, group); built = group; break;
                    case 'Arrow': buildArrow(el, diagonal, group); built = group; break;
                    case 'Point':
                        buildPoints(el, visualDiagonal, markers, scaleVector);
                        built = null;
                        break;
                    case 'BSplineCurve':
                        buildCurve(el, diagonal, group, makeBSplineCurve(el));
                        built = group;
                        break;
                    case 'BezierCurve': buildBezier(el, diagonal, group); built = group; break;
                    case 'Text': built = null; break;
                    default: built = null; break;
                }
                if (el.type === 'Text') {
                    var textSprite = makeTextSprite(el.text, {
                        fontSize: el.fontSize, fontFamily: el.fontFamily,
                        fontWeight: el.fontWeight, fontStyle: el.fontStyle,
                        color: cssColor(el.color, '#000000')
                    });
                    textSprite.position.set(el.position[0], el.position[1], el.position[2]);
                    if (el.matrix && el.matrix.length === 16) {
                        textSprite.position.applyMatrix4(new THREE.Matrix4().fromArray(el.matrix));
                    }
                    textSprite.position.multiply(scaleVector);
                    textSprite.userData.constantSize = true;
                    markers.add(textSprite);
                    continue;
                }
                if (!built) { continue; }
                if (el.matrix && el.matrix.length === 16) {
                    built.applyMatrix4(new THREE.Matrix4().fromArray(el.matrix));
                }
                objects.add(built);
            }
        }
        if (clipPlanes.length > 0) {
            // the contents are cut; the box and the axes are added elsewhere and stay whole
            objects.traverse(function (node) {
                if (node.material) {
                    var materials = Array.isArray(node.material) ? node.material : [node.material];
                    for (var mi = 0; mi < materials.length; mi++) {
                        materials[mi].clippingPlanes = clipPlanes;
                        materials[mi].clipShadows = true;
                    }
                }
            });
            addClipPlaneSurfaces(objects, data, clipPlanes, scaleVector, visualMin, visualMax);
        }

        scene.add(objects);
        scene.add(markers);

        // --- camera

        var viewPoint = data.viewPoint || [1.3, -2.4, 2.0];
        var viewVertical = data.viewVertical || [0, 0, 1];
        var orthographic = data.viewProjection === 'Orthographic';
        var fov = typeof data.viewAngle === 'number' ? data.viewAngle : 35;

        // ViewPoint is given in a box whose longest side is 1, so the distance scales with maxDim
        var direction = new THREE.Vector3(viewPoint[0], viewPoint[1], viewPoint[2]);
        if (direction.lengthSq() === 0) { direction.set(1.3, -2.4, 2.0); }
        var distance = direction.length() * maxDim;
        var target = data.viewCenter
            ? new THREE.Vector3(data.viewCenter[0], data.viewCenter[1], data.viewCenter[2])
                .multiply(scaleVector)
            : center;

        // SphericalRegion fits the sphere around the scene rather than the scene itself, so the
        // picture keeps its size however the scene is turned
        var spherical = data.sphericalRegion === true;
        var radius = visualSize.length() / 2;

        var camera;
        if (orthographic) {
            var half = (spherical ? radius : visualSize.length() * 0.6) * 1.05;
            var aspect = width / height;
            camera = new THREE.OrthographicCamera(-half * aspect, half * aspect, half, -half,
                0.01, distance * 100);
        } else {
            camera = new THREE.PerspectiveCamera(fov, width / height, maxDim * 0.01, maxDim * 100);
            // widen the distance if the box would not fit in the field of view
            var fitDistance = spherical
                ? radius / Math.sin(fov * Math.PI / 360)
                : visualSize.length() / (2 * Math.tan(fov * Math.PI / 360));
            distance = Math.max(distance, fitDistance * 1.05);
        }
        // ViewRange keeps only what lies between two distances from the camera
        if (data.viewRange && data.viewRange.length >= 2) {
            camera.near = Math.max(1e-6, data.viewRange[0]);
            camera.far = data.viewRange[1];
            camera.updateProjectionMatrix();
        }
        camera.up.set(viewVertical[0], viewVertical[1], viewVertical[2]);
        camera.position.copy(target).add(direction.clone().normalize().multiplyScalar(distance));
        camera.lookAt(target);
        scene.add(camera);

        // ViewMatrix says outright what the other view options describe. Wolfram writes its
        // matrices a row at a time, which is the order Matrix4.set takes them in.
        var fixedCamera = false;
        if (data.viewTransform && data.viewTransform.length === 16) {
            var t = data.viewTransform;
            var view = new THREE.Matrix4().set(t[0], t[1], t[2], t[3], t[4], t[5], t[6], t[7],
                t[8], t[9], t[10], t[11], t[12], t[13], t[14], t[15]);
            camera.matrixAutoUpdate = false;
            camera.matrixWorldInverse.copy(view);
            camera.matrixWorld.copy(view).invert();
            camera.matrix.copy(camera.matrixWorld);
            var p = data.viewProjectionMatrix;
            if (p && p.length === 16) {
                camera.projectionMatrix.set(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7],
                    p[8], p[9], p[10], p[11], p[12], p[13], p[14], p[15]);
                camera.projectionMatrixInverse.copy(camera.projectionMatrix).invert();
            }
            fixedCamera = true;
        }

        var controls = new THREE.OrbitControls(camera, renderer.domElement);
        controls.enableDamping = true;
        controls.dampingFactor = 0.12;
        controls.target.copy(target);
        if (fixedCamera) {
            // turning the scene would undo the matrices the call gave
            controls.enabled = false;
        } else {
            controls.update();
        }

        // --- lights

        if (data.lights) {
            for (var li = 0; li < data.lights.length; li++) {
                var spec = data.lights[li];
                var light = null;
                var intensity = (typeof spec.intensity === 'number' ? spec.intensity : 1)
                    * WOLFRAM_LIGHT_INTENSITY;
                // The colour is passed as written, so three.js reads it the same way it reads a
                // surface colour. Taking the value as a linear coefficient instead washes the
                // shading out: the face that should be a deep magenta comes back pale lavender.
                var colour = spec.color;
                if (spec.type === 'AmbientLight') {
                    light = new THREE.AmbientLight(colour, intensity);
                } else if (spec.type === 'DirectionalLight') {
                    light = new THREE.DirectionalLight(colour, intensity);
                    if (spec.position) {
                        light.position.set(spec.position[0], spec.position[1], spec.position[2]);
                    }
                } else if (spec.type === 'PointLight') {
                    light = new THREE.PointLight(colour, intensity, spec.distance || 0,
                        spec.decay || 1);
                    if (spec.position) {
                        light.position.set(spec.position[0], spec.position[1], spec.position[2])
                            .multiply(scaleVector);
                    }
                } else if (spec.type === 'SpotLight') {
                    light = new THREE.SpotLight(colour, intensity, spec.distance || 0,
                        spec.angle || Math.PI / 4, 0.2, spec.decay || 1);
                    if (spec.position) {
                        light.position.set(spec.position[0], spec.position[1], spec.position[2])
                            .multiply(scaleVector);
                    }
                    if (spec.target) {
                        var targetObject = new THREE.Object3D();
                        targetObject.position.set(spec.target[0], spec.target[1], spec.target[2])
                            .multiply(scaleVector);
                        scene.add(targetObject);
                        light.target = targetObject;
                    }
                }
                if (!light) { continue; }
                if (spec.fixedToCamera) {
                    // the light travels with the camera so a surface keeps its shading while it is
                    // being rotated, which is what Wolfram's automatic lighting does
                    camera.add(light);
                    if (light.isDirectionalLight) {
                        // a directional light points from its position at its target, and the
                        // default target sits at the world origin; aiming it at the camera instead
                        // keeps the direction fixed in camera space as the camera moves
                        light.target = camera;
                    }
                } else {
                    scene.add(light);
                }
            }
        }

        // --- box, axes, ticks

        var decoration = new THREE.Group();
        scene.add(decoration);
        var sprites = [];

        var axesColor = typeof data.axesColor === 'number' ? data.axesColor : 0x000000;
        var boxColor = typeof data.boxColor === 'number' ? data.boxColor : 0xa0a0a0;
        var labelColor = cssColor(data.labelColor, '#000000');
        var tickColorCss = cssColor(typeof data.ticksColor === 'number' ? data.ticksColor
            : data.labelColor, '#000000');
        var labelFontSize = typeof data.labelFontSize === 'number' ? data.labelFontSize : 12;
        var labelFontFamily = data.labelFontFamily || 'Arial, sans-serif';

        if (data.boxed) {
            var boxGeometry = new THREE.BoxGeometry(visualSize.x || 1e-6, visualSize.y || 1e-6,
                visualSize.z || 1e-6);
            var edges = new THREE.EdgesGeometry(boxGeometry);
            var boxLines = new THREE.LineSegments(edges,
                new THREE.LineBasicMaterial({ color: boxColor }));
            boxLines.position.copy(center);
            decoration.add(boxLines);
        }

        if (data.faceGrids) {
            addFaceGrids(decoration, visualBox, data, ranges, scaleVector);
        }

        var axesGroup = new THREE.Group();
        decoration.add(axesGroup);

        /** The corners of the visual box, indexed by the sign pattern of each coordinate. */
        function corner(sx, sy, sz) {
            return new THREE.Vector3(sx > 0 ? visualMax.x : visualMin.x,
                sy > 0 ? visualMax.y : visualMin.y, sz > 0 ? visualMax.z : visualMin.z);
        }

        /**
         * Choose which of the four parallel box edges an axis is drawn on. Wolfram re-chooses as
         * the scene turns so the axis stays on the silhouette and never runs through the middle of
         * the object; x and y take the edge that sits lowest on screen, z the leftmost one.
         */
        function chooseEdge(axis) {
            var explicit = data.axesEdge && data.axesEdge[axis];
            var candidates = [];
            var signs = [[-1, -1], [-1, 1], [1, -1], [1, 1]];
            for (var c = 0; c < signs.length; c++) {
                var s = signs[c];
                if (Array.isArray(explicit) && (explicit[0] !== s[0] || explicit[1] !== s[1])) {
                    continue;
                }
                var start, end;
                if (axis === 0) {
                    start = corner(-1, s[0], s[1]); end = corner(1, s[0], s[1]);
                } else if (axis === 1) {
                    start = corner(s[0], -1, s[1]); end = corner(s[0], 1, s[1]);
                } else {
                    start = corner(s[0], s[1], -1); end = corner(s[0], s[1], 1);
                }
                candidates.push({ start: start, end: end });
            }
            if (candidates.length === 0) { return null; }
            if (candidates.length === 1) { return candidates[0]; }

            var best = null, bestScore = -Infinity;
            for (var k = 0; k < candidates.length; k++) {
                var mid = candidates[k].start.clone().add(candidates[k].end).multiplyScalar(0.5);
                var projected = mid.clone().project(camera);
                var score = axis === 2 ? -projected.x : -projected.y;
                if (score > bestScore) { bestScore = score; best = candidates[k]; }
            }
            return best;
        }

        function buildAxes() {
            while (axesGroup.children.length > 0) {
                var child = axesGroup.children.pop();
                if (child.material && child.material.map) { child.material.map.dispose(); }
                if (child.material) { child.material.dispose(); }
                if (child.geometry) { child.geometry.dispose(); }
            }
            sprites = sprites.filter(function (s) { return s.userData.constantSize; });

            var tickLength = maxDim * 0.02;
            for (var axis = 0; axis < 3; axis++) {
                if (!data.axes || !data.axes[axis]) { continue; }
                if (data.axesEdge && data.axesEdge[axis] === 'None') { continue; }
                var edge = chooseEdge(axis);
                if (!edge) { continue; }

                axesGroup.add(new THREE.Line(
                    new THREE.BufferGeometry().setFromPoints([edge.start, edge.end]),
                    new THREE.LineBasicMaterial({ color: axesColor })));

                // ticks point away from the box, so they never cut into the surface
                var mid = edge.start.clone().add(edge.end).multiplyScalar(0.5);
                var outward = mid.clone().sub(center);
                outward.setComponent(axis, 0);
                if (outward.lengthSq() < 1e-12) {
                    outward.set(axis === 2 ? -1 : 0, 0, axis === 2 ? 0 : -1);
                }
                outward.normalize();

                var ticks = (data.ticks && data.ticks[axis]) || [];
                var lo = ranges[axis][0];
                var span = ranges[axis][1] - ranges[axis][0];
                for (var t = 0; t < ticks.length; t++) {
                    var fraction = span > 0 ? (ticks[t].position - lo) / span : 0.5;
                    var point = edge.start.clone().lerp(edge.end, fraction);
                    var tip = point.clone().add(outward.clone().multiplyScalar(tickLength));
                    axesGroup.add(new THREE.Line(
                        new THREE.BufferGeometry().setFromPoints([point, tip]),
                        new THREE.LineBasicMaterial({ color: axesColor })));

                    var label = makeTextSprite(ticks[t].label, {
                        fontSize: labelFontSize, fontFamily: labelFontFamily,
                        color: tickColorCss
                    });
                    label.position.copy(point.clone()
                        .add(outward.clone().multiplyScalar(tickLength * 2.6)));
                    label.userData.constantSize = true;
                    axesGroup.add(label);
                    sprites.push(label);
                }

                var axisLabel = data.axesLabel && data.axesLabel[axis];
                if (axisLabel) {
                    var sprite = makeTextSprite(axisLabel, {
                        fontSize: labelFontSize * 1.15, fontFamily: labelFontFamily,
                        fontWeight: 'bold', color: labelColor
                    });
                    sprite.position.copy(mid.clone()
                        .add(outward.clone().multiplyScalar(tickLength * 6)));
                    sprite.userData.constantSize = true;
                    axesGroup.add(sprite);
                    sprites.push(sprite);
                }
            }
            updateSpriteScales(sprites, height, camera);
        }

        buildAxes();

        // constant-size text primitives need scaling too
        markers.traverse(function (child) {
            if (child.userData && child.userData.constantSize) { sprites.push(child); }
        });
        updateSpriteScales(sprites, height, camera);

        if (data.plotLabel) {
            addOverlay(container, data.plotLabel, labelColor, labelFontSize * 1.2);
        }
        if (data.showLegend && data.legendText) {
            addLegend(container, data.legendText);
        }

        // --- loop

        var lastCameraKey = '';
        var frame = 0;
        var disposed = false;
        function currentCameraKey() {
            return camera.position.x.toFixed(3) + ',' + camera.position.y.toFixed(3) + ','
                + camera.position.z.toFixed(3);
        }

        function animate() {
            if (disposed) { return; }
            frame = requestAnimationFrame(animate);
            controls.update();
            var key = currentCameraKey();
            if (key !== lastCameraKey) {
                lastCameraKey = key;
                // the chosen box edges depend on where the camera is
                buildAxes();
            }
            renderer.render(scene, camera);
        }
        animate();

        function resize() {
            var w = container.clientWidth, h = container.clientHeight;
            if (!w || !h) { return; }
            width = w;
            height = h;
            if (camera.isOrthographicCamera) {
                var half = visualSize.length() * 0.6;
                var aspect = w / h;
                camera.left = -half * aspect;
                camera.right = half * aspect;
                camera.top = half;
                camera.bottom = -half;
            } else {
                camera.aspect = w / h;
            }
            camera.updateProjectionMatrix();
            renderer.setSize(w, h);
            updateSpriteScales(sprites, h, camera);
        }
        var resizeObserver = null;
        if (typeof ResizeObserver === 'function') {
            resizeObserver = new ResizeObserver(resize);
            resizeObserver.observe(container);
        } else {
            global.addEventListener('resize', resize);
        }

        return {
            dispose: function () {
                disposed = true;
                cancelAnimationFrame(frame);
                if (resizeObserver) {
                    resizeObserver.disconnect();
                } else {
                    global.removeEventListener('resize', resize);
                }
                controls.dispose();
                scene.traverse(function (object) {
                    if (object.geometry) { object.geometry.dispose(); }
                    var material = object.material;
                    if (!material) { return; }
                    var list = Array.isArray(material) ? material : [material];
                    for (var i = 0; i < list.length; i++) {
                        if (list[i].map) { list[i].map.dispose(); }
                        list[i].dispose();
                    }
                });
                // frees the GPU context, which is the whole point of tearing the scene down
                renderer.dispose();
                renderer.forceContextLoss();
                while (container.firstChild) { container.removeChild(container.firstChild); }
            }
        };
    }

    /** Grid lines on the back faces of the bounding box. */
    function addFaceGrids(parent, box, data, ranges, scaleVector) {
        var THREE = global.THREE;
        var color = typeof data.faceGridsColor === 'number' ? data.faceGridsColor : 0xcccccc;
        var material = new THREE.LineBasicMaterial({ color: color, transparent: true, opacity: 0.8 });
        var min = box.min, max = box.max;
        for (var axis = 0; axis < 3; axis++) {
            var ticks = (data.ticks && data.ticks[axis]) || [];
            var lo = ranges[axis][0];
            var span = ranges[axis][1] - ranges[axis][0];
            if (span <= 0) { continue; }
            for (var t = 0; t < ticks.length; t++) {
                var fraction = (ticks[t].position - lo) / span;
                var value = [min.x, min.y, min.z][axis]
                    + fraction * ([max.x, max.y, max.z][axis] - [min.x, min.y, min.z][axis]);
                var points = [];
                if (axis === 0) {
                    points = [new THREE.Vector3(value, min.y, min.z),
                        new THREE.Vector3(value, max.y, min.z),
                        new THREE.Vector3(value, max.y, min.z),
                        new THREE.Vector3(value, max.y, max.z)];
                } else if (axis === 1) {
                    points = [new THREE.Vector3(min.x, value, min.z),
                        new THREE.Vector3(min.x, value, max.z),
                        new THREE.Vector3(min.x, value, min.z),
                        new THREE.Vector3(max.x, value, min.z)];
                } else {
                    points = [new THREE.Vector3(min.x, min.y, value),
                        new THREE.Vector3(min.x, max.y, value),
                        new THREE.Vector3(min.x, min.y, value),
                        new THREE.Vector3(max.x, min.y, value)];
                }
                parent.add(new THREE.LineSegments(
                    new THREE.BufferGeometry().setFromPoints(points), material));
            }
        }
    }

    function ensureRelative(container) {
        if (global.getComputedStyle(container).position === 'static') {
            container.style.position = 'relative';
        }
    }

    function addOverlay(container, text, color, fontSize) {
        ensureRelative(container);
        var div = document.createElement('div');
        div.style.cssText = 'position:absolute;top:6px;left:0;width:100%;text-align:center;'
            + 'pointer-events:none;font-family:Arial,sans-serif;';
        div.style.color = color;
        div.style.fontSize = fontSize + 'px';
        div.textContent = text;
        container.appendChild(div);
    }

    function addLegend(container, text) {
        ensureRelative(container);
        var div = document.createElement('div');
        div.style.cssText = 'position:absolute;top:10px;right:10px;background:rgba(255,255,255,.85);'
            + 'padding:4px 6px;border:1px solid #ccc;font:12px Arial,sans-serif;pointer-events:none;';
        div.textContent = text;
        container.appendChild(div);
    }

    global.renderSymjaWebGL = renderSymjaWebGL;

    // Snippets emitted before three.js finished loading park themselves on this queue.
    global.SymjaWebGLQueue = global.SymjaWebGLQueue || [];
    global.drainSymjaWebGLQueue = function () {
        var queue = global.SymjaWebGLQueue || [];
        while (queue.length > 0) {
            var entry = queue.shift();
            try {
                renderSymjaWebGL(entry[0], entry[1]);
            } catch (error) {
                // a page carries many graphics, and one that cannot be built must not stop the
                // rest of them from appearing
                console.error('symja_webgl: ' + entry[0] + ': ' + error);
            }
        }
    };
    if (global.THREE) {
        global.drainSymjaWebGLQueue();
    }

})(window);
