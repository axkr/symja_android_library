//TODO: error handling?
//TODO: no scaled coords yet
//TODO: color as array? -> color converter
//TODO: size scaling, dynamic coords problematic

function drawGraphics2d(id, json) {
    console.log(json);
    // TODO: additional parameters for initialisation
    // initialize the jsx board
    var board = JXG.JSXGraph.initBoard(id, { boundingbox: [-8, 8, 8, -8], axis: true, showClearTraces: true });
    // draw every element in the json
    for (element of json.elements) {
        drawGraphic(board, element);
    }
}

function drawGraphic(board, json) {
    // change the color format for jxg graph
    if (json.color != null) {
        json.color = JXG.rgb2hex(json.color);
    }
    switch (json.type) {
        case "point":
            drawPoint(board, json);
            break;
        case "line":
            drawLine(board, json);
            break;
        case "disk":
            drawCircle(board, json, 1);
            break;
        case "circle":
            drawCircle(board, json, 0);
            break;
        default:
            console.log("Type " + json.type + " not recognized"); 
    }
}

function drawPoint(board, json) {
    for (coord of json.coords) {
        board.create("point", coord[0], {
            strokeColor: json.color,
            fillColor: json.color,
            strokeOpacity: json.opacity,
            fillOpacity: json.opacity,
            size: json.pointSize,
        });
    }
}

function drawCircle(board, json, filled) {
    for (coord of json.coords) {
        // calculate the foci of the ellipse
        var foci = calculateFoci(json.radius1, json.radius2, coord[0]);
        board.create("ellipse", [foci[0], foci[1], foci[2]], {
            strokeColor: json.color,
            fillColor: json.color,
            strokeOpacity: json.opacity,
            fillOpacity: json.opacity * filled,
        });
    }
}

function drawLine(board, json) {
    for (index = 1; index < json.coords.length; index++) {
        //TODO: line between dynamic points instead of fixed coords?
        //TODO: additional directives: width, dashed, gap
        board.create(
            "line",
            [json.coords[index][0], json.coords[index - 1][0]],
            {
                straightFirst: false,
                straightLast: false,
                strokeColor: json.color,
            }
        );
    }
}

function calculateFoci(radiusX, radiusY, coords) {
    var eccentricity;
    if (radiusX > radiusY) {
        eccentricity = JXG.Math.nthroot(1 - radiusY / radiusX,2);
        return [
            [eccentricity * radiusX + coords[0], coords[1]],
            [-eccentricity * radiusX + coords[0], coords[1]],
            radiusX*2,
        ];
    } else {
        eccentricity = JXG.Math.nthroot(1 - radiusX / radiusY,2);
        return [
            [coords[0], eccentricity * radiusY + coords[1]],
            [coords[0], -eccentricity * radiusY + coords[1]],
            radiusY*2,
        ];
    }
}

function testRun() {
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
                radius1: 1.0,
                radius2: 4.0,
                coords: [[[3.0, 4.0]]],
            },
            {
                type: "line",
                color: [1.0, 0.5, 0.0],
                opacity: 1.0,
                coords: [
                    [[1.0, 1.0]],
                    [[3.0, 1.0]],
                    [[4.0, 3.0]],
                    [[4.0, 7.0]],
                ],
            },
            {
                type: "point",
                color: [0.7, 1.0, 0.0],
                coords: [[[0, 0]], [[1, 1]], [[2, 2]], [[3, 3]]],
                opacity: 0.5,
                pointSize: 3,
            },
        ],
    });
}
