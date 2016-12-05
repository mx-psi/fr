
all: trabajo.pdf presentacion.pdf

trabajo.pdf: trabajo.md bibliografia.tex
	pandoc $< -o $@

presentacion.pdf: presentacion.tex
	pdflatex $< -o $@
	pdflatex $< -o $@

%.tex: %.md
	pandoc $^ -o $@

clean:
	rm -f *.snm *.log *.aux *.nav *.toc *.out
