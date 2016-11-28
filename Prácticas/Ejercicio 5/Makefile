
all:
	javac *.java

pdfs: Pres.pdf Memoria.pdf

%.pdf: %.tex
	pdflatex $<

%.pdf: %.md
	pandoc $< -o $@

clean:
	rm -f *.class *.aux *.snm *.log *.out *.vrb *.toc *.nav
