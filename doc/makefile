JOB ?= bachelor_paper
# trailing / is important below
OUTDIR := ./

TEX_MAIN ?= $(JOB)
TEX := pdflatex -shell-escape -output-directory $(OUTDIR) -jobname $(JOB)
BIB := bibtex
IDX := makeindex
VIEW := "xdg-open"
CWD := $(shell pwd)
TEX_OUT := $(OUTDIR)$(JOB).pdf

all: view

fast:
	$(TEX) $(TEX_MAIN).tex
	"$(VIEW)" "$(CWD)/$(TEX_OUT)"

view: $(TEX_OUT)
	"$(VIEW)" "$(CWD)/$(TEX_OUT)"
	
clean:
	rm -f $(OUTDIR)*.aux $(OUTDIR)*.bcf $(OUTDIR)*.bbl $(OUTDIR)*-blx.bib $(OUTDIR)*.run.xml $(OUTDIR)*.idx $(OUTDIR)*.ilg $(OUTDIR)*.lot $(OUTDIR)*.lof $(OUTDIR)*.lol $(OUTDIR)*.blg $(OUTDIR)*.alg $(OUTDIR)*.ind $(OUTDIR)*.toc $(OUTDIR)*.acl $(OUTDIR)*.acn $(OUTDIR)*.acr $(OUTDIR)*.out $(OUTDIR)*.log $(OUTDIR)*.gls $(OUTDIR)*.glo $(OUTDIR)*.glg $(OUTDIR)*.ist $(OUTDIR)*.brf $(OUTDIR)*.ver $(OUTDIR)*.hst $(OUTDIR)*.glsdefs $(OUTDIR)*.bib.bak

$(JOB).pdf: *.tex $(wildcard content/*.tex) $(wildcard demo/*.tex) $(wildcard *.bib)
	$(TEX) $(TEX_MAIN)
	if grep "\\citation" $(OUTDIR)*.aux ; then \
		$(BIB) $(OUTDIR)$(JOB); \
	fi
	$(TEX) $(TEX_MAIN)
	$(TEX) $(TEX_MAIN)

.PHONY = clean view all fast
