
all: trabajo.pdf

trabajo.pdf: trabajo.md bibliografia.tex
	pandoc $< -o $@

%.tex: %.md
	pandoc $^ -o $@
