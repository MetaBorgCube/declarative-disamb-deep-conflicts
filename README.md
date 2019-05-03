# A Direct Semantics for Declarative Disambiguation of Expression Grammars

This repository contains the SDF3 grammars for Tiger, Jasmin, IceDust, OCaml and Java. These grammars have been used to evaluate the implementation of contextual grammars.

## Contents

- [SGLR parser with support for data-dependent parsing](https://github.com/MetaBorgCube/declarative-disamb-deep-conflicts/tree/master/org.spoofax.jsglr2).
- [Parse Table Generator](https://github.com/MetaBorgCube/declarative-disamb-deep-conflicts/tree/master/org.metaborg.sdf2table).
- Grammars:
	 - [Tiger](https://github.com/MetaBorgCube/declarative-disamb-deep-conflicts/tree/master/Tiger)
	 - [IceDust](https://github.com/MetaBorgCube/declarative-disamb-deep-conflicts/tree/master/IceDust)
	 - [Jasmin](https://github.com/MetaBorgCube/declarative-disamb-deep-conflicts/tree/master/Jasmin)
	 - [Java](https://github.com/MetaBorgCube/declarative-disamb-deep-conflicts/tree/master/Java)
	 - [OCaml](https://github.com/MetaBorgCube/declarative-disamb-deep-conflicts/tree/master/OCaml)
	 - [Test](https://github.com/MetaBorgCube/declarative-disamb-deep-conflicts/tree/master/Test)

## Installation

Spoofax is distributed as an Eclipse plugin. We point to the [Spoofax Installation Guide](http://www.metaborg.org/en/latest/source/install.html) for further information. You may use the information in that guide, but please download Spoofax + Eclipse from one of the links below, according to your platform.

- Windows 32-bits
- Windows 64-bits
- Linux 32-bits
- Linux 32-bits
- macOS

## Using Spoofax

The documentation page for the [Spoofax Language Workbench](http://www.metaborg.org/en/latest/index.html) contains a very intuitive guide on how to define programming languages using  Spoofax. Below, we list a few steps to use the syntax definition formalism [SDF3](http://www.metaborg.org/en/latest/source/langdev/meta/lang/tour/syntax.html) to generate a parser and use it inside Spoofax.

The syntax of each language in our benchmark is specified in the respective language project, under the folder `/syntax`. This folder contains a main file named `<Language-Name>.SDF3`, which may import additional SDF3 modules to specify the language's syntax. To try out the parser, build the project (if any errors occur, cleaning may be necessary), and open a example file (under the folder `/example`), running the menu `Spoofax->Syntax->Show Parsed AST` shows the abstract syntax tree constructed after parsing the program. Any changes in the grammar are applied to the language files after re-building the project. We also created a dummy Spoofax project (`/Test`) to be used as playground for defining other grammars and testing the parser.
