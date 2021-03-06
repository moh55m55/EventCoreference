EventCoreference
================
version 3.1.2
Copyright: VU University Amsterdam, Piek Vossen
email: piek.vossen@vu.nl
website: www.newsreader-project.eu
website: cltl.nl

SOURCE CODE:
https://github.com/cltl/EventCoreference

INSTALLATION
1. git clone https://github.com/cltl/EventCoreference
2. cd EventCoreference
3. chmod +wrx install.sh
4. run the install.sh script

The install.sh will build the binary through apache-maven-3.x and the pom.xml and move it to the "lib" folder.

REQUIREMENTS
EventCoreference is developed in Java 1.6 and can run on any platform that supports Java 1.6.
You need to first install maven (version 3.x) to install EventCoreference: https://maven.apache.org/index.html.

LICENSE
    EventCoreference is free software: you can redistribute it and/or modify
    it under the terms of the The Apache License, Version 2.0:
        http://www.apache.org/licenses/LICENSE-2.0.txt.

    EventCoreference is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

USAGE
To find out about the parameters that can be set for NAF2SEM you can run the naf2sem-usage.sh script.

DESCRIPTION
EventCoreference is a package that contain 3 main functionalities:

1. NAF event-coreference: resolving event coreference within a single document in NAF format and adding event coreference sets to the coreferece layer in NAF
2. NAF to SEM-GRaSP RDF-TRiG: converting NAF files to SEM/GRaSP RDF-TRiG files in which events, participants and time are represented as instances with relations with pointers to their mentions in the text. 
Each mention of an instance or relation is enriched with information on the source and the source perspective. Possibly these functions can handle cross-document
event coreference: representing the event data across identicanl events across documents in a single instance representation.

Typically, the 2 functions are called in sequence, assuming that there is a collection of NAF files or a NAF stream with the necessary layers present:

NAF -<NAF-event-coreference>--> NAF -<NAF2SEM-GRaSP>--> RDF-TRiG -<RDF-TRiG2StoryLines>--> JSON -<StoryTeller>--> Browser Visualisation

Depending on the configuration, the processing can be done for single NAF files, a stream of NAF files, a batch folder with NAF files or through interaction with a KnowledgeStore repository in the back.
The same is true for processing RDF-TRiG data. Interaction with the KnowledgeStore requires population of the KnowledgeStore node with the NAF files anf the RDF-TRiG files.

We further describe the main functions in more detail below.

1. NAF-event-coreference (within-document event-coreference):

Requires the presences of a SRL layer in NAF and for some scripts a term layer with wordnet synsets and a wordnet LMF resource.

The standard scripts for creating an event coreference layer inside the NAF file is:

- event-coreference-en.sh (for English texts)
- event-coreference-nl.sh (for Dutch texts)
- event-coreference-lemma.sh (any language)
- event-coreference-singleton.sh (any language)

The scripts for English and Dutch use a wordnet in LMF format for determining similarity across events and 
a list of FrameNet frames to determine which events qualify for event coreference. 
The lemma script only groups events with the same lemma (baseline) and the singleton script creates singleton coreference sets 
from all predicates in the SRL. These last two scripts do not require a wordnet and can be ran on any language.

Call any of the above scripts as follows:

cat ../example/wikinews_1173_en.naf | ./event-coreference-en.sh
cat ../example/wikinews_1173_nl.naf | ./event-coreference-nl.sh
cat ../example/wikinews_1173_en.naf | ./event-coreference-lemma.sh
cat ../example/wikinews_1173_en.naf | ./event-coreference-singleton.sh

The result of the with document event coreference is a NAF file where the coreference layer contains the event coreference sets.

The scripts can be adapted to run the functions with different settings by setting parameters. These parameters are explained in the usage documentation.

2. Conversion from NAF to SEM-GRaSP RDF-TRiG and cross-document event-coreference

Another set of functions reads the NAF files and creates SEM-GRasSP TRiG files (RDF format). 
While doing this it converts the mention-based representations in NAF into instance-based representations.
The input NAF files minimally need the following layers:
- tokens, terms, entities, coreference for events, srl, timeExpressions

If any of these layers is absent or empty, the RDF-TRiG files will be empty.
To create an event coreference layer, use the event-coreference scripts described above.

There are two sets of functions:

2.1 Multiple NAF document conversion

It reads a batch of NAF files and extracts Composite Events from each NAF to store them in cluster files as binary object data. 
Composite events are data structures that contain the action, all the participants and timex anchors relevant for the event. 
In a second step it reads the binary object data to compare the event descriptions. 
If identity is established the event descriptions are merged. 
If not they remain separate. At the end the results is serialized to SEM-GRaSP TRiG files, 
where each cluster folder gets a single TRiG file as a result. 
Multiple document conversion is typically used for large batches of NAF files. Scripts:

naf2sem-batch-cluster.sh
naf2sem-batch-nocluster.sh

The naf2sem-batch-cluster.sh script creates subfolders "contextualEvent", "sourceEvent", "grammaticalEvent" and "futureEvent". Within each of the folders, it creates temporal buckets with the events that have the same temporal anchoring. All events within a bucket are compared and a single TRiG file is created as output in each bucket.

The naf2sem-batch-nocluster.sh script creates a single cluster folder "all" and compare all events.

Call any of these scripts as follows:

./naf2sem-batch-cluster.sh ../example test
./naf2sem-batch-nocluster.sh ../example test

The scripts can be adapted to run the functions with different settings by setting parameters. 
These parameters are explained in the usage documentation.


2.2 Single document conversion

**Note: Make sure that you have your KnowledgeStore (KS) docker set up prior to running the single document streaming mode. You can get the docker from https://github.com/dkmfbk/knowledgestore-docker/. Once your KS docker is running, update the script with the correct KS endpoint for NAF2SEM, following the pattern: $YOUR_SERVER_URL/custom/naf2sem .**

Instead of cross-document extraction, there is also a function that takes a single NAF input stream or file and directly creates the SEM instance and GRaSP perspective representations. 
For each input stream an output stream is generated in RDF-TRiG format (similar for single file input/output).
In a next step the RDF TRiG output stream can be sent to a KnowledgeStore (direct population of the RDF-TRiG) and the KnowledgeStore is queried for similar events.
For identical events owl:sameAs links are created that are also stored in the KnowledgeStore. This set up can be used for
an end-to-end streaming set up. Scripts:

naf2sem-grasp.sh (file or stream)
run_single_naf.sh (NAF stream and direct interaction with the KnowledgeStore)

The naf2sem-grasp.sh script reads a NAF file and generates the RDF-TRiG output stream. Example usage:

cat ../example/wikinews_1173_en.naf.coref.xml | ./naf2sem-grasp.sh

The run_single_naf.sh script first call the GetSemFromNafStream function to get the RDF-TRiG output and then calls the KnowledgeStore
for population and determining equivalence with events in the KnowledgeStore. This script requires access to a runing KnowledgeStore.
Example usage:

cat ../example/wikinews_1173_en.naf.coref.xml | ./run_single_naf.sh

The scripts can be adapted to run the functions with different settings by setting parameters. 
These parameters are explained in the usage documentation.


REFERENCES:
P. Vossen, R. Agerri, I. Aldabe, A. Cybulska, M. van Erp, A. Fokkens, E. Laparra, A. Minard, A. P. Aprosio, G. Rigau, M. Rospocher, and R. Segers, “Newsreader: how semantic web helps natural language processing helps semantic web,” Special issue knowledge based systems, elsevier, to appear. 

M. Rospocher, M. van Erp, P. Vossen, A. Fokkens, I. Aldabe, G. Rigau, A. Soroa, T. Ploeger, and T. Bogaard, “Building event-centric knowledge graphs from news,” Journal of web semantics, 2016. 

See also:
http://kyoto.let.vu.nl/newsreader_deliverables/NWR-D5-1-3.pdf


