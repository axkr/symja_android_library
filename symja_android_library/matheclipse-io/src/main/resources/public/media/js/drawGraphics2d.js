//TODO: top/bottom with inequality

//TODO: documentation in md
function createGraphics2dDiv(json, maxWidth, maxHeight) {
    var json2dDiv, maxRatio, givenRatio, width, height;
    json2dDiv = document.createElement("div");
    maxRatio = maxHeight / maxWidth;
    if (json.aspectRatio === undefined) {
        width = maxWidth;
        height = maxHeight;
    } else {
        if (json.aspectRatio.symbol === undefined) {
            if (json.aspectRatio.factor === undefined) {
                givenRatio = json.aspectRatio.factor;
                // width dominates
                if (givenRatio < maxRatio) {
                    width = maxWidth;
                    height = maxWidth * givenRatio;
                }
                // height dominates
                else {
                    height = maxHeight;
                    width = maxHeight / givenRatio;
                }
            }
        } else if (json.aspectRatio.symbol == "automatic") {
            givenRatio =
                json.extent === undefined
                    ? 1
                    : (json.extent.ymax - json.extent.ymin) / (json.extent.xmax - json.extent.xmin);
            // width dominates
            if (givenRatio < maxRatio) {
                width = maxWidth;
                height = maxWidth * givenRatio;
            }
            // height dominates
            else {
                height = maxHeight;
                width = maxHeight / givenRatio;
            }
        } else if (json.aspectRatio.symbol == "full") {
            width = maxWidth;
            height = maxHeight;
        }
    }

    json2dDiv.setAttribute("width", width);
    json2dDiv.setAttribute("height", height);

    drawGraphics2d(json2dDiv.id, json);

    return json2dDiv;
}

function drawGraphics2d(id, json) {
    var myoptions, extent, axes, grid, board, opts;
    myoptions = {
        elements: { dragToTopOfLayer: true },
        polygon: { vertices: { layer: 5 }, borders: { layer: 5 } },
        layer: {
            text: 5,
            point: 5,
            glider: 9,
            arc: 5,
            line: 5,
            circle: 5,
            curve: 5,
            turtle: 5,
            polygon: 5,
            sector: 3,
            angle: 5,
            integral: 5,
            axis: 3,
            ticks: 2,
            grid: 1,
            image: 5,
            trace: 0,
        },
    };
    JXG.Options = JXG.merge(JXG.Options, myoptions);

    extent = json.extent === undefined ? { xmin: -9.0, xmax: 9.0, ymin: -9.0, ymax: 9.0 } : json.extent;
    axes = json.axes === undefined ? { hasaxes: false, scaling: ["None", "None"], grid: false } : json.axes;
    grid = json.axes.grid ? -1 : 5;

    board = JXG.JSXGraph.initBoard(id, {
        boundingbox: [extent.xmin, extent.ymax, extent.xmax, extent.ymin],
        //axis: json.axes.hasaxes,
        axis: json.axes.hasaxes === true,
        defaultAxes: {
            x: { ticks: { visible: true, majorHeight: grid } },
            y: { ticks: { visible: true, majorHeight: grid } },
        },
        keepaspectratio: false,
        showClearTraces: true,
        showCopyRight: false,
        grid: false,
    });
    opts = { graphicsComplex: false, extent: extent };

    // draw every element in the json
    drawAxes(board, json.axes, extent);
    for (element of json.elements) {
        drawGraphic(board, element, opts);
    }
}

function drawGraphic(board, json, opts) {
    var args;

    switch (json.type) {
        case "point":
            args = getArgs(["coords", "color", "opacity", "filling", "pointSize"], json, opts, json.type);
            drawPoint(board, args);
            break;
        case "arrow":
        case "line":
            args = getArgs(["coords", "color", "opacity", "filling", "arrow", "thickness"], json, opts, json.type);
            drawLine(board, args);
            break;
        case "disk":
        case "circle":
            args = getArgs(
                ["coords", "color", "opacity", "radius1", "radius2", "angle1", "angle2", "filled"],
                json,
                opts,
                json.type
            );
            drawCircle(board, args);
            break;
        case "rectangle":
            args = getArgs(["coords", "color", "opacity"], json, opts, json.type);
            drawRectangle(board, args);
            break;
        case "polygon":
            args = getArgs(["coords", "color", "opacity"], json, opts, json.type);
            drawPolygon(board, args);
            break;
        case "text":
            args = getArgs(["coords", "color", "opacity", "texts", "fontSize"], json, opts, json.type);
            drawText(board, args);
            break;
        case "graphicscomplex":
            drawGraphicsComplex(board, json, opts);
            break;
        case undefined:
            setOption(json, opts);
            break;
        default:
            console.warn("Type " + json.type + " not supported");
    }
}

function drawGraphicsComplex(board, json, opts) {
    opts.graphicsComplex = true;
    opts.graphicsComplexCoords = json.coords;
    for (element of json.data) {
        drawGraphic(board, element, opts);
    }
    opts.graphicsComplex = false;
}

function drawAxes(board, json, extent) {
    var attr = JXG.Options.axis,
        conversionX = function (n) {
            return n;
        };
    conversionY = function (n) {
        return n;
    };
    if (json === undefined || json.hasaxes === true) return;

    if (json.hasaxes[0]) {
        var xAxis = board.create(
            "line",
            [
                [0, 0],
                [1, 0],
            ],
            attr
        );
        conversionX = drawTicks(board, xAxis, json, extent.xmax - extent.xmin, false);
    }

    if (json.hasaxes[1]) {
        var yAxis = board.create(
            "line",
            [
                [0, 0],
                [0, 1],
            ],
            attr
        );
        conversionY = drawTicks(board, yAxis, json, extent.ymax - extent.ymin, extent, true);
    }

    if (json.hasaxes[0] || json.hasaxes[1])
        board.highlightInfobox = function (x, y, el) {
            this.infobox.setText("(" + conversionX(x) + ", " + conversionY(y) + ")");
        };
}

function drawTicks(board, axis, json, length, index) {
    var attr = index ? JXG.Options.board.defaultAxes.y.ticks : JXG.Options.board.defaultAxes.x.ticks,
        conversion,
        scaling = json.scaling === undefined ? ["None", "None"] : json.scaling,
        coordIndex = index ? 1 : 0;
    switch (scaling[coordIndex]) {
        case "log":
            attr.drawZero = false;
            conversion = function (n) {
                return +Math.exp(n).toFixed(2);
            };
            break;
        case "log2":
            attr.drawZero = false;
            conversion = function (n) {
                return Math.pow(2,n);
            };
            break;
        case "log10":
            attr.drawZero = false;
            conversion = function (n) {
                return Math.pow(10, n);
            };
            break;
        default:
            conversion = function (n) {
                return n;
            };
            break;
    }
    attr.generateLabelText = function (tick, zero) {
        var n = Math.round(tick.usrCoords[coordIndex + 1] - zero.usrCoords[coordIndex + 1]);
        console.log(n);
        return conversion(n).toString();
    };
    attr.drawLabels = true;
    attr.majorHeight = json.grid ? -1 : 5;
    board.create("ticks", [axis, calculateSetOff(length)], attr);
    return conversion;
}

function setOption(json, opts) {
    opts[json.option] = validateAttr(json.option, json.value, undefined);
}

function getAttr(attr, json, opts, type) {
    var value;
    if (attr == "coords") value = convertCoords(opts.graphicsComplex ? json["positions"] : json["coords"], opts);
    else if (json[attr] != undefined) value = validateAttr(attr, json[attr], type);
    else if (opts[attr] != undefined) value = opts[attr];
    else value = validateAttr(attr, undefined, type);
    return value;
}

function validateAttr(attr, value, type) {
    switch (attr) {
        case "color":
            if (value == undefined) value = [0.0, 0.0, 0.0];
            else value = convertColor(value);
            break;
        case "opacity":
            if (value == undefined) value = 1.0;
            break;
        case "pointSize":
            if (value == undefined) value = 0.005;
            break;
        case "fontSize":
            if (value == undefined) value = 20;
            break;
        case "radius1":
        case "radius2":
            if (value == undefined) value = 1;
            break;
        case "arrow":
            value = type == "arrow";
            break;
        case "filled":
            value = type == "disk";
            break;
        default:
    }
    return value;
}

function getArgs(lst, json, opts, type) {
    var args = {};
    for (attr of lst) {
        args[attr] = getAttr(attr, json, opts, type);
    }
    return args;
}

function drawPoint(board, args) {
    var fillingCoord, infiniteLength;
    for (coord of args.coords) {
        board.create("point", coord, {
            strokeColor: args.color,
            fillColor: args.color,
            strokeOpacity: args.opacity,
            fillOpacity: args.opacity,
            fixed: true,
            name: "",
            size: (board.canvasWidth * args.pointSize) / 2,
        });
        switch (args.filling) {
            case "top":
                fillingCoord = [coord[0], coord[1] + 1];
                infiniteLength = true;
                break;
            case "bottom":
                fillingCoord = [coord[0], coord[1] - 1];
                infiniteLength = true;
                break;
            case "mid":
                fillingCoord = [coord[0], 0];
                infiniteLength = false;
                break;
            default:
                continue;
        }
        board.create("line", [coord, fillingCoord], {
            strokeColor: args.color,
            straightFirst: false,
            straightLast: infiniteLength,
            strokeOpacity: args.opacity,
            fixed: true,
            filled: true,
        });
    }
}

function drawCircle(board, args) {
    for (coord of args.coords) {
        // calculate the foci of the ellipse
        var foci = calculateFoci(args.radius1, args.radius2, coord);
        board.create("ellipse", [foci[0], foci[1], foci[2], args.angle1, args.angle2], {
            strokeColor: args.color,
            fillColor: args.color,
            strokeOpacity: args.opacity,
            fillOpacity: args.opacity * args.filled,
        });
    }
}

function drawLineSegmented(board, args) {
    for (index = 1; index < args.coords.length; index++) {
        board.create("line", [args.coords[index], args.coords[index - 1]], {
            straightFirst: false,
            straightLast: false,
            strokeColor: args.color,
        });
    }
}

function drawLine(board, args) {
    //TODO: additional directives: width, dashed, gap
    //debugger
    var newCoords = convertCoordsCurve(args.coords),
        inverted = true,
        xCopy,
        yCopy,
        coordCopy,
        curve = board.create("curve", newCoords, {
            lastArrow: args.arrow,
            strokeColor: args.color,
            strokeOpacity: args.opacity,
            strokeWidth: (board.canvasWidth * args.thickness) / 2,
        });
    switch (args.filling) {
        case "bottom":
            inverted = false;
        case "top":
            //FIXME: inequality not possible
            //board.create("inequality", [curve], { fillColor: args.color, opacity: args.opacity, inverted: inverted });
            break;
        case "mid":
            xCopy = [...newCoords[0]];
            yCopy = [...newCoords[1]];
            coordCopy = [xCopy, yCopy];
            coordCopy[0].push(coordCopy[0][coordCopy[0].length - 1]);
            coordCopy[0].unshift(coordCopy[0][0]);
            coordCopy[1].push(0);
            coordCopy[1].unshift(0);

            board.create("curve", coordCopy, {
                strokeOpacity: 0.0,
                fillColor: args.color,
                fillOpacity: args.opacity,
                highlightStrokeColorOpacity: 0.0,
            });
            break;
    }
}

function drawPolygon(board, args) {
    board.create("polygon", args.coords, {
        fillColor: args.color,
        strokeOpacity: args.opacity,
        fillOpacity: args.opacity,
        borders: { strokeColor: args.color },
        vertices: { fixed: true, visible: false },
    });
}

function drawText(board, args) {
    for (index in args.coords) {
        board.create("text", [args.coords[index][0], args.coords[index][1], args.texts[index]], {
            color: args.color,
            fixed: true,
            opacity: args.opacity,
            fontSize: args.fontSize,
        });
    }
}

function drawRectangle(board, args) {
    var start, end, p1, p2, p3, p4;
    start = args.coords[0];
    if (args.coords.length == 1) end = [start[0] + 1, start[1] + 1];
    else if (args.coords.length == 2) end = args.coords[1];

    p1 = board.create("point", [start[0], start[1]], { visible: false });
    p2 = board.create("point", [end[0], end[1]], { visible: false });
    p3 = board.create(
        "point",
        [
            function () {
                return p1.X();
            },
            function () {
                return p2.Y();
            },
        ],
        { visible: false }
    );
    p4 = board.create(
        "point",
        [
            function () {
                return p2.X();
            },
            function () {
                return p1.Y();
            },
        ],
        { visible: false }
    );

    board.create("polygon", [p1, p3, p2, p4], {
        strokeColor: args.color,
        fillColor: args.color,
        strokeOpacity: args.opacity,
        fillOpacity: args.opacity,
        fixed: true,
    });
}

function calculateFoci(radiusX, radiusY, coords) {
    var eccentricity, diff;
    diff = Math.abs(radiusX * radiusX - radiusY * radiusY);
    eccentricity = Math.sqrt(diff);
    if (radiusX > radiusY) {
        return [
            [eccentricity + coords[0], coords[1]],
            [-eccentricity + coords[0], coords[1]],
            [coords[0], radiusY + coords[1]],
        ];
    } else {
        diff = radiusY ^ (2 - radiusX) ^ 2;
        return [
            [coords[0], eccentricity + coords[1]],
            [coords[0], -eccentricity + coords[1]],
            [coords[0], radiusY + coords[1]],
        ];
    }
}

function convertColor(rgb) {
    var color = [];
    if (rgb != null) {
        color[0] = Number((rgb[0] * 255).toFixed(0));
        color[1] = Number((rgb[1] * 255).toFixed(0));
        color[2] = Number((rgb[2] * 255).toFixed(0));
        color = JXG.rgb2hex(color);
    }
    return color;
}

function convertCoords(coords, opts) {
    var x,
        y,
        newCoords = [],
        target = opts.graphicsComplex ? opts.graphicsComplexCoords : coords,
        key;

    for (index in coords) {
        key = opts.graphicsComplex ? coords[index] - 1 : index;
        if (target[key][0] != null) newCoords[index] = target[key][0];
        else {
            x = opts.extent.xmin + target[key][1][0] * (opts.extent.xmax - opts.extent.xmin);
            y = opts.extent.ymin + target[key][1][1] * (opts.extent.ymax - opts.extent.ymin);
            newCoords[index] = [x, y];
        }
    }
    return newCoords;
}

function convertCoordsCurve(coords) {
    var x = [];
    var y = [];
    for (key in coords) {
        x[key] = coords[key][0];
        y[key] = coords[key][1];
    }
    return [x, y];
}

function calculateSetOff(length) {
    var setOff = Math.floor(Math.log10(Math.abs(length) / 2));
    return 10 ** setOff;
}

function testRun() {
    /*
    drawGraphics2d("graphics2d", {
        elements: [
            {
                option: "opacity",
                value: 1.0,
            },
            {
                option: "pointSize",
                value: 0.0013,
            },
            {
                option: "textSize",
                value: 12,
            },
            {
                option: "fontSize",
                value: 12,
            },
            {
                option: "color",
                value: [0.0, 0.0, 0.0],
            },
            {
                type: "graphicscomplex",
                coords: [
                    [null, [1.0, 0.5]],
                    [null, [0.5, 0.1]],
                    [null, [0.0, 0.5]],
                    [null, [0.5, 0.6]],
                ],
                data: [
                    {
                        type: "polygon",
                        positions: [1, 2, 3, 4],
                    },
                ],
            },
        ],
        extent: {
            xmin: -20.0,
            xmax: 10.0,
            ymin: -10.0,
            ymax: 15.0,
        },
        axes: {
            hasaxes: [true, true],
            grid: false
        },
        aspectRatio: {
            symbol: "automatic",
        },
    });
    */

    /**
        elements: [
            { option: "opacity", value: 1.0 },
            { option: "pointSize", value: 0.0013 },
            { option: "textSize", value: 12 },
            { option: "color", value: [1.0, 0.0, 0.0] },
            {
                type: "disk",
                radius1: 1.0,
                radius2: 1.0,
                coords: [[[0.0, 0.0]]],
            },
            { option: "color", value: [0.0, 1.0, 0.0] },
            { type: "rectangle", coords: [[[0.0, 0.0]], [[2.0, 2.0]]] },
            { option: "color", value: [0.0, 0.0, 1.0] },
            {
                type: "disk",
                radius1: 1.0,
                radius2: 1.0,
                coords: [[[2.0, 2.0]]],
            },
        ],
        extent: { xmin: -1.0, xmax: 3.0, ymin: -1.0, ymax: 3.0 },
        axes: { hasaxes: false },
        aspectRatio: { symbol: "automatic" },
    });
    */
    drawGraphics2d("graphics2d", {
        elements: [
            {
                type: "disk",
                color: [0.3, 0.0, 0.8],
                opacity: 0.5,
                radius1: 2.0,
                radius2: 2.0,
                coords: [[[1.0, 0.0]]],
            },
            {
                type: "circle",
                color: [0.2, 0.9, 0.0],
                opacity: 0.9,
                radius1: 4.0,
                radius2: 2.0,
                coords: [[[0.0, 5.0]]],
            },
            {
                type: "circle",
                color: [0.0, 0.0, 1.0],
                opacity: 1.0,
                radius1: 4.0,
                radius2: 2.0,
                angle1: 0.523598775598298,
                angle2: 2.35619449019234,
                coords: [[[0.0, 5.0]]],
            },
            { option: "filling", value: "top" },
            {
                type: "line",
                color: [1.0, 0.5, 0.0],
                opacity: 0.6,
                coords: [[[1.0, 1.0]], [[3.0, 1.0]], [[4.0, 3.0]], [[5.0, 7.0]]],
                thickness: 0.01,
            },
            { option: "filling", value: "top" },
            {
                type: "point",
                color: [0.7, 1.0, 0.0],
                coords: [[[0, 0]], [[1, 1]], [[2, 2]], [[3.5, 3.5]]],
                opacity: 0.5,
                pointSize: 0.005,
            },
            {
                type: "rectangle",
                color: [0.0, 0.5, 1.0],
                opacity: 1.0,
                coords: [[[2.0, -5.0]], [[4.0, -2.0]]],
            },
            {
                type: "arrow",
                color: [0.2, 0.0, 1.0],
                opacity: 1.0,
                coords: [[[0.0, 0.0]], [[-4.0, 3.0]]],
                thickness: 0.02,
            },
            {
                type: "polygon",
                color: [1.0, 0.5, 0.0],
                opacity: 1.0,
                coords: [[[-1.0, -1.0]], [[0.0, -1.0]], [[-4.0, -4.0]], [[-1.0, 0.0]]],
            },
            { option: "pointSize", value: 0.01 },
            { option: "filling", value: "none" },
            { option: "color", value: [1, 0, 1] },
            {
                type: "point",
                coords: [[[-1, -1]], [[-2, -2]], [[-3, -3]]],
                opacity: 0.5,
            },
            {
                type: "text",
                color: [0.5, 0.4, 0.1],
                fontSize: 40,
                opacity: 0.8,
                coords: [[[-5, -5]], [[5, 5]]],
                texts: ["Bottom left", "Top right"],
            },
        ],
        extent: { xmin: -9.0, xmax: 9.0, ymin: -9.0, ymax: 9.0 },
        axes: { hasaxes: [true, true], scaling: ["none", "log10"] },
    });
    /*
    drawGraphics2d("graphics2d", {
        axes: {
            hasaxes: false,
        },
        elements: [
            {
                option: "opacity",
                value: 1.0,
            },
            {
                option: "pointSize",
                value: 0.0013,
            },
            {
                option: "textSize",
                value: 12,
            },
            {
                option: "fontSize",
                value: 12,
            },
            {
                option: "color",
                value: [0.0, 0.0, 0.0],
            },
            {
                aspectRatio: {
                    symbol: "automatic",
                },
            },
            {
                type: "graphicscomplex",
                coords: [[[0.0, 0.0]], [[2.0, 0.0]], [[2.0, 2.0]], [[0.0, 2.0]]],
                data: [
                    {
                        type: "circle",
                        color: [0.0, 0.0, 0.0],
                        positions: [1],
                    },
                    {
                        type: "circle",
                        color: [0.0, 0.0, 0.0],
                        positions: [2],
                    },
                    {
                        type: "circle",
                        color: [0.0, 0.0, 0.0],
                        positions: [3],
                    },
                    {
                        type: "circle",
                        color: [0.0, 0.0, 0.0],
                        positions: [4],
                    },
                ],
            },
        ],
        extent: {
            xmin: 0.0,
            xmax: 2.0,
            ymin: 0.0,
            ymax: 2.0,
        },
    });
    */
}
