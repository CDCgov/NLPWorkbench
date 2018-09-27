/**
    LISP2DOT JS  parse lisp into graphviz dot file (http://www.graphviz.org/Documentation/dotguide.pdf)
    Copyright   Chunqi SHI (https://github.com/chunqishi)
    License     MIT / http://bit.ly/mit-license
    Version     v0.0.1
*/
    var findPair = function (s, map, obj) {
        var stack = [];
        var cnt = 0;
        for (var i = 0; i < s.length; i++) {
            if(s[i] == obj) {
                cnt ++;
                if(cnt % 2 == 0) {
                    map[stack.pop()] = i;
                } else {
                    stack.push(i);
                }
            }
        }
        return map;
    }

    var findMatch = function (s, map, left, right) {
        var stack = [];
        for (var i = 0; i < s.length; i++) {
            if(s[i] == left) {
                stack.push(i);
            } else if(s[i] == right) {
                map[stack.pop()] = i;
            }
        }
        return map;
    }

    var isAlphaNumeric = function (chr) {
        return /^[a-z0-9]+$/i.test(chr);
    }

    var isWhitespace = function (chr) {
        return /\s+/.test(chr);
    }


    var parseBlock = function (s, start, end, pos_map) { // [start, end] are exactly location pair.
        var blocks = [];
        for (var i = start; i <= end; i ++) {
            if (i in pos_map) { // has mapping location
                blocks.push([i,pos_map[i]]);
                i = pos_map[i];
            } else if(!isWhitespace(s[i])) {
                var j = i + 1;
                while(!isWhitespace(s[j]) && j <= end) {
                    j++;
                }
                if(j > i)
                    blocks.push([i,j - 1]);
                i = j - 1;
            }
        }
        return blocks;
    }

    var parseChild = function (s, start, end, pos_map) {
        var p_start = start; // parentheses
        while(s[p_start]!="(" && p_start <= end) {
            p_start ++;
        }
        if (p_start <= end) {
            // alert("find parentheses " + p_start +" " + pos_map[p_start]);
            return parseBlock(s, p_start + 1, pos_map[p_start] - 1, pos_map);
        }
        return [];
    }

    var lisp2tree = function(lisp, start, end, pos_map, tree, tree_con) {
        var sub = lisp.substring(start, end + 1);
        if (sub.indexOf("(") < 0 ) {  // end node
            //tree_con[start] = [start, end];
            tree_con[start] = lisp.substring(start, end+1);
            return start;
        } else {
            var children = parseChild(lisp, start, end, pos_map);
            var tree_children = [];
            for(var i = 1; i < children.length; i ++) {
                var tree_child = lisp2tree(lisp, children[i][0], children[i][1], pos_map, tree, tree_con);
                tree_children.push(tree_child);
            }
            tree[children[0][0]] = tree_children;
            //tree_con[children[0][0]] = children[0];
            tree_con[children[0][0]] = lisp.substring(children[0][0], children[0][1]+1);
            return children[0][0];
        }
    }

    var tree2dot = function (tree, tree_con, root) {
        var dot = "digraph simpleDotFile { \n";
        var stack = [];
        stack.push(root);
        while(stack.length > 0) {
            var node = stack.pop();
            dot += "node"+node+" [label=\""+tree_con[node]+"\""
            if(node in tree) {
                dot += ", shape=box";
            } else {
                if(tree_con[node].indexOf("[") >= 0 ) {
                    dot += ", style=dotted";
                } else {
                    dot += ", color=blue";
                }
            }
            dot += "];\n";

            if(node in tree) {
                for (var i = 0; i < tree[node].length; i++) {
                    stack.push(tree[node][i]);
                    dot += "node"+node+" -> node"+tree[node][i]+";\n";
                }
            }
        }
        dot += "}\n";
        return dot;
    }

    var lisp2dot = function (lisp){
        // alert(isWhitespace("*"));
        var pos_map = {};
        findPair(lisp, pos_map, "\"");
        findPair(lisp, pos_map, "\'");
        findMatch(lisp, pos_map, "(", ")");
        findMatch(lisp, pos_map, "[", "]");
        // var children = parseChild(lisp, 0   , lisp.length, pos_map);
        // alert(JSON.stringify(children));
        var tree = {}, tree_con = {};
        var root = lisp2tree(lisp, 0, lisp.length - 1, pos_map, tree, tree_con);
        //alert(JSON.stringify(tree));
        //alert(JSON.stringify(tree_con));
        var dot = tree2dot(tree, tree_con, root);
        // alert(dot);
        return dot;
    }