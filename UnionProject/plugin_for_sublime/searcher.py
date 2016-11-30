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
		cursorPosition = self.view.rowcol(start_region.begin())[1]
		line = self.view.line(start_region)
		string = self.view.substr(line)
		funcName = ""
		buf = ""
		firstFuncName = ""
		isFirst = True
		beginPos = 0
		endPos = 0
		separators = [' ', '\t', '=', '+', '-', '*', '/', '&', '|']
		constructions = ["if", "for", "while"]
		for c in string:
			if (c in separators):
				endPos += 1
				beginPos = endPos
				buf = ""
				continue
			if (c == '('):
				if (buf in constructions):
					endPos += 1
					beginPos = endPos
					buf = ""
					continue
				if (buf in separators):
					endPos += 1
					beginPos = endPos
					buf = ""
					continue
				if ((beginPos <= cursorPosition) and (cursorPosition <= endPos)):
					funcName = buf
					break
				else:
					if (isFirst):
						firstFuncName = buf
						isFirst = False
					endPos += 1
					beginPos = endPos
					buf = ""
					continue
			endPos += 1
			buf += c
		path = self.view.file_name()
		lineNumber = (cursor + 1)
		if (funcName == ""):
			funcName = firstFuncName
		url = "http://localhost:8080/set_example?func=%s&path=%s&line=%s&string=%s" % (funcName, path, lineNumber, string)
		print(url)
		if (funcName != ""):
			requests.get(url)
		else:
			print("No function name on this line!")