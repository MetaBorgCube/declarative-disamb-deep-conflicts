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

We provide 3 different alternatives for running the experiment.

### Running the experiment Locally

It is possible to run the experiment locally by cloning this repository. This option requires that you have Java and Maven installed. Make sure of copying (or creating) the file `settings.xml` from the root of this project to your Maven settings file (`~/.m2/settings.xml`).
Run the experiment by executing `mvn clean verify` or by loading the projects using the top-level `pom.xml` file into your favorite IDE through Maven. In the IDE, run the class `Benchmarks.java` in the project `disambiguation-bench`, adding `-Xms512m -Xmx1024m -Xss16m -XX:MaxPermSize=512m` as running configurations.

#### Installing Spoofax Locally

Spoofax is distributed as an Eclipse plugin. We point to the [Spoofax Installation Guide](http://www.metaborg.org/en/latest/source/install.html) for further information. You may use the information in that guide, but please download Spoofax + Eclipse from one of the links below, according to your platform (all versions are bundled with an embedded Java Runtime Environment - JRE).

- [Windows 32-bits](https://drive.google.com/open?id=1dTSZKa7fH_r-F2IWKBfOhF575x3vvL88)
- [Windows 64-bits](https://drive.google.com/open?id=1IFma9c3tpg-ketaR2pBHDZVcjNhodZ0V)
- [Linux 32-bits](https://drive.google.com/open?id=19jeovkqf8QfUvo5n9Bn1eqSrnLrvZJ4Z)
- [Linux 64-bits](https://drive.google.com/open?id=10If4iS6NBlvC7gdPOZ_xDky7Z327ik_3)
- [macOS](https://drive.google.com/open?id=1PYfNmE2eaB9Af4k9OauN2T6sKoFVZyBv)

#### Using Spoofax

The documentation page for the [Spoofax Language Workbench](http://www.metaborg.org/en/latest/index.html) contains a very intuitive guide on how to define programming languages using  Spoofax. Below, we list a few steps to use the syntax definition formalism [SDF3](http://www.metaborg.org/en/latest/source/langdev/meta/lang/tour/syntax.html) to generate a parser and use it inside Spoofax.

The syntax of each language in our benchmark is specified in the respective language project, under the folder `/syntax`. This folder contains a main file named `<Language-Name>.SDF3`, which may import additional SDF3 modules to specify the language's syntax. To try out the parser, build the project (if any errors occur, cleaning may be necessary), and open a example file (under the folder `/example`), running the menu `Spoofax->Syntax->Show Parsed AST` shows the abstract syntax tree constructed after parsing the program. Any changes in the grammar are applied to the language files after re-building the project. We also created a dummy Spoofax project (`/Test`) to be used as playground for defining other grammars and testing the parser.

### Virtual Box (Recommended)

We created a VM using [Virtual Box](https://www.virtualbox.org) (version 6.0.10) with Spoofax/Eclispe installed, containing all the projects in the default workspace.
Download the VM [here](https://drive.google.com/open?id=1i90No5V0E5fgDl5iK5iTEhyUphTlaCgl) and run it with VirtualBox (password: artifact) with memory set to at least 4.0GiB.

To run the experiment, open the terminal and run `mvn clean verify` on the folder `declarative-disamb-deep-conflicts`. Optionally, open the application `eclipse-spoofax` from the Desktop and run the class `Benchmarks.java` in the project `disambiguation-bench`.

The Eclipse installation in the VM has Spoofax pre-installed. Please, consider the instructions above on how to use Spoofax. The language projects are located in the working set `spoofax-grammars`.

### Docker

To run the benchmark using Docker, make sure you have [Docker](https://www.docker.com) and [git-lfs](https://git-lfs.github.com) installed. When cloning this repository, make sure to pull the correct Docker file by running `git lfs pull`. Load the container and run the experiment using the following commands (with the host memory preferences in docker set to at least 4.0GiB):

 		 docker load < decl-disamb-docker.tar
		 docker run -it decl-disamb-v1
		 cd declarative-disamb-deep-conflicts
		 mvn clean verify

Note that this option does not allow running Spoofax. Instead, run it locally (installation details above) or using the VirtualBox VM.
