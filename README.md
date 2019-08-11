# A Direct Semantics for Declarative Disambiguation of Expression Grammars

This repository contains the SDF3 grammars for Tiger, Jasmin, IceDust, OCaml and Java. These grammars have been used to evaluate the implementation of contextual grammars.

## Contents

- [SGLR parser with support for data-dependent parsing](https://github.com/MetaBorgCube/declarative-disamb-deep-conflicts/tree/master/org.spoofax.jsglr2).
- [Parse Table Generator](https://github.com/MetaBorgCube/declarative-disamb-deep-conflicts/tree/master/org.metaborg.sdf2table).
- Grammars:
	 - [Tiger](https://github.com/MetaBorgCube/declarative-disamb-deep-conflicts/tree/master/spoofax-language-projects/Tiger)
	 - [IceDust](https://github.com/MetaBorgCube/declarative-disamb-deep-conflicts/tree/master/spoofax-language-projects/IceDust)
	 - [Jasmin](https://github.com/MetaBorgCube/declarative-disamb-deep-conflicts/tree/master/spoofax-language-projects/Jasmin)
	 - [Java](https://github.com/MetaBorgCube/declarative-disamb-deep-conflicts/tree/master/spoofax-language-projects/Java)
	 - [OCaml](https://github.com/MetaBorgCube/declarative-disamb-deep-conflicts/tree/master/spoofax-language-projects/OCaml)
	 - [Miscellaneous Grammars](https://github.com/MetaBorgCube/declarative-disamb-deep-conflicts/tree/master/spoofax-language-projects/Misc)

## Running the Benchmark

To run the benchmark, make sure you have [Docker](https://www.docker.com) and [git-lfs](https://git-lfs.github.com) installed. Load container and run the experiment using the following commands (with the host memory preferences in docker set to at least 4.0GiB):

 		 docker load < decl-disamb-docker.tar
		 docker run -it decl-disamb-v1
		 cd declarative-disamb-deep-conflicts
		 mvn clean verify


## Installing Spoofax

Spoofax is distributed as an Eclipse plugin. We point to the [Spoofax Installation Guide](http://www.metaborg.org/en/latest/source/install.html) for further information. You may use the information in that guide, but please download Spoofax + Eclipse from one of the links below, according to your platform (all versions are bundled with an embedded Java Runtime Environment - JRE).

- [Windows 32-bits](https://drive.google.com/open?id=1_DPIwgiTqGupnWlmY17Oqr0ZvHpsiOaA)
- [Windows 64-bits](https://drive.google.com/open?id=1VqzRAsj2Dj6_BJYJ_uI6x4YH5LsDHgrN)
- [Linux 32-bits](https://drive.google.com/open?id=1C34fBDoklbexDWZZyerdoqohblJJyZx_)
- [Linux 64-bits](https://drive.google.com/open?id=1iNMR-imke7vpXkjE4TJhnOe2GWnTyYsX)
- [macOS](https://drive.google.com/open?id=1B1yUrN_5XKRDPp6EK8q-s5p6CG93Kaz8)

## Using Spoofax

The documentation page for the [Spoofax Language Workbench](http://www.metaborg.org/en/latest/index.html) contains a very intuitive guide on how to define programming languages using  Spoofax. Below, we list a few steps to use the syntax definition formalism [SDF3](http://www.metaborg.org/en/latest/source/langdev/meta/lang/tour/syntax.html) to generate a parser and use it inside Spoofax.

The syntax of each language in our benchmark is specified in the respective language project, under the folder `/syntax`. This folder contains a main file named `<Language-Name>.SDF3`, which may import additional SDF3 modules to specify the language's syntax. To try out the parser, build the project (if any errors occur, cleaning may be necessary), and open a example file (under the folder `/example`), running the menu `Spoofax->Syntax->Show Parsed AST` shows the abstract syntax tree constructed after parsing the program. Any changes in the grammar are applied to the language files after re-building the project. We also created a dummy Spoofax project (`/Test`) to be used as playground for defining other grammars and testing the parser.
