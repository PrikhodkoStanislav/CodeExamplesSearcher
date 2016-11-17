import sys
import os.path
sys.path.append(os.path.join(os.path.dirname(__file__), "requests-2.11.1"))

import requests
import sublime
import sublime_plugin

class SearchCommand(sublime_plugin.TextCommand):
	def run(self, edit):
		start_region = self.view.sel()[0]
		cursor = self.view.rowcol(start_region.begin())[0]
		line = self.view.line(start_region)
		string = self.view.substr(line)
		funcName = ""
		buf = ""
		for c in string:
			if c == ' ':
				buf = ""
			if c == '(':
				if (buf == "if") or (buf == "for") or (buf == "while"):
					buf = ""
				else:
					funcName = buf
					break
			buf += c
		path = self.view.file_name()
		url = "http://localhost:8080/set_example?func=%s,path=%s,line=%s" % (funcName, path, cursor + 1)
		print(url)
		requests.get(url)