/**
 * Copyright 2014, Emory University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
<!--
const FONT_ID     = "10px Arial";
const FONT_FORM   = "13px Arial";
const FONT_LEMMA  = "13px Arial";
const FONT_POS    = "12px Arial";
const FONT_DEPREL = "12px Arial";
const DELIM_FIELD = "\t";
const DELIM_NODE  = "\n";
const DELIM_FEAT  = "|";
const BLANK       = "_";

const ANCHOR_W    = 4;
const ANCHOR_H    = 8;

const ID_GAP_W = 1;
const ID_GAP_H = 2;

const ARC_RADIUS  = 5;
const ARC_GAP_H   = 20;
const ARC_GAP_W   = ARC_RADIUS * 2;

const FORM_GAP_W  = 25;
const FORM_GAP_H  = 12;

const DEPREL_MARGIN_W = 40;
const DEPREL_SHIFT_W  = 4;
const DEPREL_SHIFT_H  = 4;

const INIT_X = 25;
const INIT_Y = 40;

// ----------------------------------- Geometries for lexica -----------------------------------

function RLexicon(ctx, node)
{
    var deprel, max = 0;

    this.w_form   = getFontWidth(ctx, FONT_FORM  , node.form);
    this.w_lemma  = getFontWidth(ctx, FONT_LEMMA , node.lemma);
    this.w_pos    = getFontWidth(ctx, FONT_POS   , node.pos);
    this.w_deprel = getFontWidth(ctx, FONT_DEPREL, node.deprel);
    this.w_max    = Math.max(this.w_form, this.w_lemma, this.w_pos);
}

RLexicon.prototype.setMinX = function(x)
{
    this.x_min = x;
};

RLexicon.prototype.addMinX = function(x)
{
    this.x_min += x;
};

RLexicon.prototype.getMinX = function()
{
    return this.x_min;
};

RLexicon.prototype.getMaxX = function()
{
    return this.x_min + this.w_max;
};

RLexicon.prototype.getCenterX = function()
{
    return this.x_min + 0.5 * this.w_max;
};

RLexicon.prototype.getIdX = function()
{
    return this.getFormX() + this.w_form;
};

RLexicon.prototype.getFormX = function()
{
    return this._getX(this.w_form);
};

RLexicon.prototype.getLemmaX = function()
{
    return this._getX(this.w_lemma);
};

RLexicon.prototype.getPOSX = function()
{
    return this._getX(this.w_pos);
};

RLexicon.prototype._getX = function(width)
{
    return this.x_min + 0.5 * (this.w_max - width);
};

// ----------------------------------- Geometries for edges -----------------------------------

function REdge(xd, xh, height)
{
    this.xd     = xd;
    this.xh     = xh;
    this.height = height;
}

// ----------------------------------- Dependency node -----------------------------------

function DEPNode(fields)
{
    this.id     = parseInt(fields[0]);
    this.form   = fields[1];
    this.lemma  = fields[2];
    this.pos    = fields[3];
    this._initFeats(fields[4]);
    this.headId = parseInt(fields[5]);
    this.deprel = fields[6];

    this.childIds   = [];
	this.sArguments = [];
    this.sHeads = (fields.length > 7) ? fields[7] : BLANK;
}

DEPNode.prototype._initFeats = function(field)
{
    this.rolesetId = BLANK;
    if (field == BLANK) return;

    var feats = field.split(DELIM_FEAT);
    var i, size = feats.length;
    var feat;

    for (i=0; i<size; i++)
    {
        feat = feats[i];

        if (feat.startsWith("pb="))
        {
        	this.rolesetId = feat.slice(3);
        	break;
        }
    }
};

DEPNode.prototype.toString = function()
{
    var build = [this.id, this.form, this.lemma, this.pos, this.feats, this.headId, this.deprel];
    return build.join(DELIM_FIELD);
};

DEPNode.prototype.addChildID = function(id)
{
    this.childIds.push(id);
};

DEPNode.prototype.getChildIDs = function()
{
    return this.childIds;
};

DEPNode.prototype.addArgument = function(arg)
{
    this.sArguments.push(arg);
};

DEPNode.prototype.getArguments = function()
{
    return this.sArguments;
};

DEPNode.prototype.sortArguments = function()
{
    return this.sArguments.sort();
};

// ----------------------------------- Dependency tree -----------------------------------

function DEPTree()
{
    var root = new DEPNode(["0", "root", "root", "root", "_", "-1", "none"]);
    this.d_nodes = [root];
}

DEPTree.prototype.toString = function()
{
     var i, size = this.size();
     var build = [];

     for (i=1; i<size; i++)
         build.push(this.getNode(i).toString());

     return build.join(DELIM_NODE);
};

DEPTree.prototype.addNode = function(node)
{
    this.d_nodes.push(node);
};

DEPTree.prototype.getNode = function(id)
{
    return this.d_nodes[id];
};

DEPTree.prototype.getHead = function(id)
{
    var headId = this.getNode(id).headId;
    return this.getNode(headId);
};

DEPTree.prototype.size = function()
{
    return this.d_nodes.length;
};

// Initializes all geometries needed to draw this tree to the specific canvas.
DEPTree.prototype.setCanvas = function(ctx, zoom)
{
    ctx.canvas.width = this.r_lexica[this.size()-1].getMaxX() + INIT_X + 25;
    ctx.canvas.width *= zoom;
};

// Initializes all geometries needed to draw this tree to the specific canvas.
DEPTree.prototype.initGeometries = function(ctx)
{
    var  groups   = this._getGeometriesGroups();
    this.r_lexica = this._getGeometriesLexica(ctx, groups);
    this.r_edges  = this._getGeometriesEdges(groups, this.r_lexica);
};

// Called by DEPTree.initGeometries().
DEPTree.prototype._getGeometriesGroups = function()
{
    var i, size = this.size();
    var curr, head;

    var lhs = createNestedEmptyArray(size);
    var rhs = createNestedEmptyArray(size);
    var groups = [];

    for (i=1; i<size; i++)
    {
        head = this.getHead(i);
        if (isUndefined(head)) continue;
        curr = this.getNode(i);

         if (curr.id < head.id)
         {
             lhs[head.id].push(curr.id);
             rhs[curr.id].push(head.id);
         }
         else
         {
             lhs[curr.id].push(head.id);
             rhs[head.id].push(curr.id);
         }
    }

    for (i=0; i<size; i++)
    {
        lhs[i] = lhs[i].sort(descendingOrder);
        rhs[i] = rhs[i].sort(descendingOrder);
        groups.push(lhs[i].concat(rhs[i]));
    }

    return groups;
};

// Called by DEPTree.initGeometries().
DEPTree.prototype._getGeometriesLexica = function(ctx, groups)
{
    var i, j, w, m, x, pm = 0, size = this.size();
    var rLexica = [], rect;
    var node, head;

    //  m: the extra margin when edge lines take more space than the form
    // pm: the previous extra margin
    for (i=0; i<size; i++)
    {
        rect = new RLexicon(ctx, this.getNode(i));

        m = (groups[i].length - 1) * ARC_GAP_W - rect.w_max;
        m = (m > 0) ? 0.5 * m : 0;
        x = (i > 0) ? rLexica[i-1].getMaxX() + FORM_GAP_W : INIT_X;

        rect.setMinX(x+m+pm);
        rLexica.push(rect);
        pm = m;
    }

    // if dependency label takes more space
    for (i=1; i<size; i++)
    {
        head = this.getHead(i);
        if (isUndefined(head)) continue;
        node = this.getNode(i);
        rect = rLexica[i];

        w = rect.w_deprel + DEPREL_MARGIN_W;
        m = Math.abs(rLexica[node.id].getMinX() - rLexica[head.id].getMinX());

        if (w > m)
        {
            j = (node.id > head.id) ? node.id : head.id;
            w -= m;
            for (; j<size; j++) rLexica[j].addMinX(w);
        }
    }

    // centering
/*  w = rLexica[size-1].getMaxX() - rLexica[0].getMinX();
    x = 0.5 * (ctx.canvas.width - w);
    m = x - rLexica[0].getMinX();
    alert(ctx.canvas.width);

    if (m > 0)
    {
        for (i=0; i<size; i++)
            rLexica[i].addMinX(m);
    }*/

    return rLexica;
};

// Called by DEPTree.initGeometries().
DEPTree.prototype._getGeometriesEdges = function(groups, rLexica)
{
    var i, h, xh, xd, max = 0, size = this.size();
    var heights = this._getHeights();
    var rEdges = [null];
    var curr, head;

    for (i=1; i<size; i++)
    {
        head = this.getHead(i);
        if (isUndefined(head)) continue;
        curr = this.getNode(i);
        xd   = this._getGeometriesEdgesAux(groups, rLexica, curr.id, head.id);
        xh   = this._getGeometriesEdgesAux(groups, rLexica, head.id, curr.id);
        h    = heights[i] * ARC_GAP_H;
        max  = Math.max(h, max);

        rEdges.push(new REdge(xd, xh, h));
    }

    this.maxHeight = max;
    return rEdges;
};

// Called by _getGeometriesEdges().
DEPTree.prototype._getGeometriesEdgesAux = function(groups, rLexica, id1, id2)
{
    return rLexica[id1].getCenterX() - (0.5 * (groups[id1].length - 1) - groups[id1].indexOf(id2)) * ARC_GAP_W;
};

// Called by _getGeometriesEdges().
DEPTree.prototype._getHeights = function()
{
    var i, size = this.size();
    var heights = [];

    for (i=0; i<size; i++)
        heights.push(0);

    for (i=1; i<size; i++)
        this._getHeightsProj(heights, i);

    for (i=1; i<size; i++)
        this._getHeightsNonProj(heights, i);

    return heights;
};

// Called by _getHeights().
DEPTree.prototype._getHeightsProj = function(heights, id)
{
    var curr = this.getNode(id);
    var head = this.getHead(id);
    var i, st, et, max = 0;
    var node;

    if (curr.id < head.id)
    {
        st = curr.id;
        et = head.id;
    }
    else
    {
        st = head.id;
        et = curr.id;
    }

    for (i=st; i<=et; i++)
    {
        if (i == id) continue;
        node = this.getHead(i);

        if (typeof node !== "undefined" && st <= node.id && node.id <= et)
        {
            if (heights[i] == 0) this._getHeightsProj(heights, i);
            max = Math.max(max, heights[i]);
        }
    }

    heights[id] = max + 1;
};

// Called by _getHeights().
DEPTree.prototype._getHeightsNonProj = function(heights, id)
{
    var i, j, st, et, headId, childId, childIds;
    var curr = this.getNode(id);
    var head = this.getHead(id);
    var height = heights[id];
    var node;

    if (curr.id < head.id)
    {
        st = curr.id;
        et = head.id;
    }
    else
    {
        st = head.id;
        et = curr.id;
    }

    for (i=st+1; i<et; i++)
    {
        node = this.getNode(i);
        headId = node.headId;

        if ((headId < st || headId > et) && heights[i] == height)
        {
            heights[id] += 1;
            break;
        }

        childIds = node.getChildIDs();

        for (j=childIds.length-1; j>=0; j--)
        {
            childId = childIds[j];

            if ((childId < st || childId > et) && heights[childId] == height)
            {
                heights[id] += 1;
                return;
            }
        }
    }
}

// ----------------------------------- Draw to canvas -----------------------------------

// Draws the specific dependency tree to the canvas context.
function drawTree(ctx, tree)
{
    var y = tree.maxHeight + FORM_GAP_H + INIT_Y;

    _drawLexca(ctx, tree, y);
    _drawArcs (ctx, tree, y-FORM_GAP_H);
}

// Called by drawTree(...).
function _drawLexca(ctx, tree, y)
{
    var i, j, len, size = tree.size();
    var node;
    var rect;
    var args;

    ctx.beginPath();
    ctx.font = FONT_ID;
    ctx.fillStyle = "#000000";    // black

    for (i=0; i<size; i++)
    {
        node = tree.getNode(i);
        rect = tree.r_lexica[i];
        ctx.fillText(node.id, rect.getIdX()+ID_GAP_W, y+ID_GAP_H);
    }

    ctx.closePath();

    ctx.beginPath();
    ctx.font = FONT_FORM;
    ctx.fillStyle = "#000000";    // black

    for (i=0; i<size; i++)
    {
        node = tree.getNode(i);
        rect = tree.r_lexica[i];
        ctx.fillText(node.form, rect.getFormX(), y);
    }

    ctx.closePath();

    ctx.beginPath();
    ctx.font = FONT_POS;
    ctx.fillStyle = "#009900";		// green
    y += 20;

    for (i=0; i<size; i++)
    {
        node = tree.getNode(i);
        rect = tree.r_lexica[i];
        ctx.fillText(node.pos, rect.getPOSX(), y);
    }

    ctx.closePath();

    y += 20;

    for (i=1; i<size; i++)
    {
        node = tree.getNode(i);

        if (node.rolesetId != BLANK)
        {
            rect = tree.r_lexica[i];

            ctx.beginPath();
            ctx.font = FONT_LEMMA;
            ctx.fillStyle = "#FF0000";    // red
            ctx.fillText(node.rolesetId, rect.getFormX(), y);
            ctx.closePath();

            ctx.beginPath();
            ctx.font = FONT_DEPREL;
            ctx.fillStyle = "#0000FF";    // red

			args = node.getArguments();
			len = args.length;

			for (j=0; j<len; j++)
			    ctx.fillText(args[j], rect.getFormX(), y+(j+1)*20);

            ctx.closePath();
        }
    }
}

// Called by drawTree().
function _drawArcs(ctx, tree, y)
{
    var i, yh, size = tree.size();
    var node;
    var rEdge, rLex;

    for (i=1; i<size; i++)
    {
        rEdge = tree.r_edges[i];
        rLex  = tree.r_lexica[i];
        node  = tree.getNode(i);
        yh    = y - rEdge.height;

        _drawEdges (ctx, rEdge.xh, rEdge.xd, y, yh);
        _drawDeprel(ctx, rEdge.xh, rEdge.xd, rLex.w_deprel, yh, node.deprel);
    }
}

// Called by _drawArcs().
function _drawEdges(ctx, x1, x2, y, yh, height)
{
    // arc
    ctx.beginPath();
    ctx.fillStyle = "#000000";    // black

    var xl, xr;

    if (x1 < x2) {xl = x1; xr = x2;}
    else         {xl = x2; xr = x1;}

    ctx.moveTo(xl, y-1);
    ctx.lineTo(xl, yh+ARC_RADIUS);
    ctx.arc(xl+ARC_RADIUS, yh+ARC_RADIUS, ARC_RADIUS, Math.PI, 1.5*Math.PI, false);
    ctx.lineTo(xr-ARC_RADIUS, yh);
    ctx.arc(xr-ARC_RADIUS, yh+ARC_RADIUS, ARC_RADIUS, 1.5*Math.PI, 0, false);
    ctx.lineTo(xr, y-1);
    ctx.stroke();

    ctx.closePath();

    // anchor
    ctx.beginPath();

    ctx.moveTo(x2, y);
    ctx.lineTo(x2 - ANCHOR_W, y - ANCHOR_H);
    ctx.lineTo(x2 + ANCHOR_W, y - ANCHOR_H);
    ctx.fill();

    ctx.closePath();
}

// Called by _drawArcs().
function _drawDeprel(ctx, x1, x2, w, yh, deprel)
{
    ctx.beginPath();
    ctx.font = FONT_DEPREL;

    var x = x2 - 0.5 * (x2 - x1 + w);
    var y = yh + DEPREL_SHIFT_H;

    ctx.fillStyle = "#FFFFFF";    // background: white
    ctx.fillRect(x-DEPREL_SHIFT_W, yh-DEPREL_SHIFT_H, w+2*DEPREL_SHIFT_W, 2*DEPREL_SHIFT_H);

    // label foreground
    ctx.fillStyle = "#0000FF";    // foreground: blue
    ctx.fillText(deprel, x, y);

    ctx.closePath();
}

// ----------------------------------- Helper methods -----------------------------------

function getFontWidth(ctx, font, str)
{
    ctx.beginPath();
    ctx.font = font;
    var width = ctx.measureText(str).width;
    ctx.closePath();

    return width;
}

function createArray(length, value)
{
    var array = [];
    var i;

    for (i=0; i<length; i++)
        array.push(value);

    return array;
}

function createNestedEmptyArray(length)
{
    var array = [];
    var i;

    for (i=0; i<length; i++)
        array.push([]);

    return array;
}

function descendingOrder(a, b)
{
    return b - a;
}

function isUndefined(object)
{
    typeof object !== "undefined";
}

if (typeof String.prototype.startsWith != 'function')
{
    String.prototype.startsWith = function (str)
    {
        return this.slice(0, str.length) == str;
    };
}

// ----------------------------------- Interface to HMTL5 -----------------------------------

function initDEPTrees()
{
    // initialize dependency trees
    var input = document.getElementById("ta_input").value.trim();
    var fields, lines = input.split(DELIM_NODE);
    var i, size = lines.length;
    var tree = new DEPTree();
    d_trees = [];

    for (i=0; i<size; i++)
    {
        fields = lines[i].trim().split(DELIM_FIELD);

        if (fields.length < 7)
        {
        	addDEPTree(d_trees, tree);
			tree = new DEPTree();
        }
        else
            tree.addNode(new DEPNode(fields));
    }

    addDEPTree(d_trees, tree);
    size = d_trees.length;
    if (size == 0) return;

    // initialize select options
    var ids = document.getElementById("st_treeId");
    ids.options.length = 0;

    for (i=0; i<size; i++)
    {
        opt = document.createElement("option");
        opt.value = i;
        opt.text = i;
        ids.appendChild(opt);
    }

    ids.selectedIndex = 0;
    drawDEPTree(ids.selectedIndex);
    document.addEventListener("keydown", keyDownHandler, false);
}

function addDEPTree(d_trees, tree)
{
	if (tree.size() > 1)
	{

		initChildIDs(tree);
		initSRL(tree);
		d_trees.push(tree);
	}
}

function initChildIDs(tree)
{
    var i, size = tree.size();
    var head;

    for (i=1; i<size; i++)
    {
        head = tree.getHead(i);
        if (isUndefined(head)) continue;
        head.addChildID(i);
    }
}

function initSRL(tree)
{
    var i, j, ns, size = tree.size();
    var node, head, ts, tc;

    for (i=1; i<size; i++)
    {
        node = tree.getNode(i);

        if (node.sHeads != BLANK)
        {
            ts = node.sHeads.split(";");
            ns = ts.length;

            for (j=0; j<ns; j++)
            {
                tc = ts[j].split(":");
                head = tree.getNode(tc[0]);
                head.addArgument(tc[1]+":"+i);
            }
        }
    }

    for (i=1; i<size; i++)
        tree.getNode(i).sortArguments();
}

function drawDEPTree(id)
{
    var ctx  = document.getElementById("cv_deptree").getContext("2d");
    var zoom = document.getElementById("rg_deptree").value;
    var tree = d_trees[id];

    tree.initGeometries(ctx);
    tree.setCanvas(ctx, zoom);
    ctx.scale(zoom, zoom);
	ctx.fillStyle="#FFFFFF";
    ctx.fillRect(0, 0, ctx.canvas.width*zoom, ctx.canvas.height*zoom);
    drawTree(ctx, tree);
    window.scrollTo(0, 0);
}

function clickPrevious()
{
    var ids = document.getElementById("st_treeId");

    if (ids.options.length > 0 && ids.selectedIndex > 0)
    {
    	ids.selectedIndex--;
        drawDEPTree(ids.selectedIndex);
    }
}

function clickNext()
{
    var ids = document.getElementById("st_treeId");

    if (ids.options.length > 0 && ids.selectedIndex+1 < ids.length)
    {
    	ids.selectedIndex++;
        drawDEPTree(ids.selectedIndex);
    }
}

function clickForward()
{
    var ids = document.getElementById("st_treeId");

    if (ids.options.length > 0)
    {
    	ids.selectedIndex = ids.length - 1;
        drawDEPTree(ids.selectedIndex);
    }
}

function clickBackward()
{
    var ids = document.getElementById("st_treeId");

    if (ids.options.length > 0)
    {
    	ids.selectedIndex = 0;
        drawDEPTree(ids.selectedIndex);
    }
}

function exportCanvas()
{
    var ctx = document.getElementById("cv_deptree");
    var img = ctx.toDataURL("image/png");
    window.open(img);
}

function selectTreeID()
{
    var ids = document.getElementById("st_treeId");
    drawDEPTree(ids.selectedIndex);
}

function readFile(f)
{
    var reader = new FileReader();
    reader.readAsText(f);

    reader.onload = function()
    {
        var text = reader.result;
        document.getElementById("ta_input").value = text;
        initDEPTrees();
    };
}

function keyDownHandler(event)
{
         if (event.keyCode == 33) clickPrevious();     // page up
    else if (event.keyCode == 34) clickNext();         // page down
    else if (event.keyCode == 35) clickForward();      // end
    else if (event.keyCode == 36) clickBackward();     // home
}

function loadScript(url, callback)
{
    // Adding the script tag to the head as suggested before
    var head = document.getElementsByTagName('head')[0];
    var script = document.createElement('script');
    script.type = 'text/javascript';
    script.src = url;

    // Then bind the event to the callback function.
    script.onreadystatechange = callback;
    script.onload = callback;

    // Fire the loading
    head.appendChild(script);
}
-->