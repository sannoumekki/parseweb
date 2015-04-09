Programmers commonly reuse existing frameworks or libraries to
reduce software development efforts. One common problem in reusing the existing frameworks or libraries is that the programmers know what type of object that they need, but do not know how to get that object with a specific method sequence. To help programmers to address this issue, we have developed an approach that takes queries of the form "Source object type -> Destination object type" as input, and suggests relevant method-invocation sequences that can
serve as solutions that yield the destination object from the source object given in the query. Our approach interacts with
a code search engine (CSE) to gather relevant code samples and
performs static analysis over the gathered samples to extract
required sequences. As code samples are collected on demand through CSE, our approach is not limited to queries of any specific set of frameworks or libraries. We have implemented our approach with a tool called PARSEWeb.