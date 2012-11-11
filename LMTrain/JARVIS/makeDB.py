# open a file
import argparse

parser = argparse.ArgumentParser(description='Example: makeLM -f yes-no.train')

parser.add_argument('-f', action="store", dest='filename');
results = parser.parse_args();

filename = results.filename;
print "file name: " + filename;
#filename = "Core/yes-no.train";

f = open(filename,"r");
for line in f:
	line = line.rstrip('\n');
	content = line.split(" ");
	n = len(content);
	i = 1;
	print "<s> ",
	while i <= n :
		if i < n :
			print content[i] + " ",
		else :
			print "</s>";
		i=i+1;
	

