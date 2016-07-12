all	: pdf html

html	: minexample/index.html

minexample/index.html	: minexample.dvi minexample.perl 
	latex2html -link 4 -split 5\
		-html_version "4.0,unicode" \
		-custom_titles \
		-local_icons \
		-nofootnode \
		-noinfo \
		-transparent -white \
		-up_url "../index.html" \
		-up_title "Home" \
		-top_navigation -bottom_navigation \
		-show_section_numbers \
		minexample
	chmod u+x minexample/*.html

minexample.dvi	: minexample.tex minexample-main.tex minexample.sty
	latex minexample

pdf:	minexample-a4.pdf minexample-screen.pdf

minexample-a4.pdf	: minexample-main.tex minexample-a4.tex minexample.sty
		pdflatex minexample-a4
		pdflatex minexample-a4
		pdflatex minexample-a4

minexample-screen.pdf	: minexample-main.tex minexample-screen.tex minexample.sty
		pdflatex minexample-screen
		pdflatex minexample-screen
		pdflatex minexample-screen

../minexample.zip	: *.tex *.sty minexample.perl
		cd ..; zip minexample.zip minexample/*.{sty,tex} \
		minexample/minexample.perl \
		minexample/README \
		minexample/Makefile

clean	:
	\rm -f *.{aux,idx,log,out,toc,bbl,blg,dvi}

squeaky	:
	\rm -f minexample-*.pdf
	\rm -f -r minexample
