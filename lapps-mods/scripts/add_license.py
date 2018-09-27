#! /usr/bin/env python
# -*- coding: utf-8 -*-

"""
This program is to:

"""
import sys

import re

reload(sys)
sys.setdefaultencoding('utf8')

__author__ = 'krim'
__date__ = '5/24/17'
__email__ = 'krim@brandeis.edu'

from bs4 import BeautifulSoup
from bs4 import CData
import json

tool_conf_file = open("../config/tool_conf.xml")
conf_soup = BeautifulSoup(tool_conf_file, 'lxml-xml')

tools = set()

for tool_def in conf_soup.find_all("tool"):
    tools.add(tool_def["file"])

tool_conf_file.close()

license_clauses = json.load(open("clauses.json"))
for tool in tools:
    print tool
    tool_group, tool_name = re.sub(r"\.xml$", "", tool).split("/")
    print tool_group, tool_name
    if license_clauses.has_key(tool_group):
        tool_xml_file = open("../tools/{}".format(tool))
        tool_soup = BeautifulSoup(tool_xml_file)
        help_tag = tool_soup.find("help")
        if help_tag is not None:

            old_help = ' '.join([str(e) for e in help_tag.contents])
            if "License\n" in old_help:
                new_help = CData(old_help)
            else:
                new_help = CData(old_help + "\nLicense\n-------\n\n" +
                                 license_clauses[tool_group])
        else:
            help_tag = tool_soup.new_tag("help")
            tool_soup.tool.append(help_tag)
            new_help = CData("\nLicense\n-------\n\n" +
                             license_clauses[tool_group])
        help_tag.string = ""
        help_tag.string.replace_with(new_help)
        tool_soup.tool.insert
        print tool_soup.tool
        tool_xml_file.close()

        # with open("../tools/{}".format(tool), "w") as out_file:
        #     out_file.write(str(tool_soup.tool.prettify(formatter="xml")))

