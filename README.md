# SPADe
Software development process and project management _anti-patterns (AP)_ are known and reoccurring bad solutions to common problems in their respective domains. Their description exist almost exclusively in textual form for human consumption rendering their actual detection difficult at best.

_Application Lifecycle Management (ALM)_ tools capture day-to-day reality of software development projects as a natural byproduct of their use.
**Software Process Anti-pattern** (SPA-) **Detector (SPADe)** framework extracts and stores project data from ALM tools for the purposes of detecting software process and project management APs.

Data from various ALM tools are mined by _Data Extractor (SPAtE)_ and saved in a database with the uniform _Metamodel (SPAMm)_. Gathered AP descriptions from literature are condensed in the _[Catalogue (SPACe)](https://github.com/ReliSA/Software-process-antipatterns-catalogue/blob/master/Antipatterns_catalogue.md)_, with the option of using _[Catalogue Editor (SPACEd)](https://github.com/ReliSA/Software-process-antipatterns-catalogue/blob/master/Antipatterns_catalogue.md)_ application, then operationalized over project data and detected by SPADe. Results are shown in the _[Web Interface (SPAWn)](https://github.com/ReliSA/SPADe-Web-GUI)_.

SPADe also allows for mined data exports in DOT or JSON format which can be input into _[Interactive Multimodal Graph Explorer (IMiGEr)](https://github.com/ReliSA/IMiGEr)_, where they can be investigated for more in-depth knowledge about activities, artifacts and actors in the project and their mutual relations.

# Further information
See the [ReliSA research group website](http://relisa.kiv.zcu.cz/areas/) for more context.

# Published work on SPADe related research:

\[1\] **SPAMm** - P. Pícha and P. Brada, "ALM Tool Data Usage in Software Process Metamodeling," 2016 42th Euromicro Conference on Software Engineering and Advanced Applications (SEAA), 2016, pp. 1-8, [doi: 10.1109/SEAA.2016.37](https://ieeexplore.ieee.org/abstract/document/7592768).

\[2\] **Use of ALM Data for different analysis** - P. Pícha, P. Brada, R. Ramsauer and W. Mauerer, "Towards Architect’s Activity Detection through a Common Model for Project Pattern Analysis," 2017 IEEE International Conference on Software Architecture Workshops (ICSAW), 2017, pp. 175-178, doi: [10.1109/ICSAW.2017.46](https://ieeexplore.ieee.org/abstract/document/7958478).

\[3\] **SPACe** - P. Brada and P. Picha, "Software process anti-patterns catalogue," 24th European Conference on Pattern Languages of Programs (EuroPLop), 2019, pp. 1–10, [doi: 10.1145/3361149.3361178](https://dl.acm.org/doi/abs/10.1145/3361149.3361178)

\[4\] **AP operationalization and detection approach** - P. Picha and P. Brada, "Software process anti-pattern detection in project data," 24th European Conference on Pattern Languages of Programs (EuroPLop), 2019, pp. 1–12, doi: [10.1145/3361149.3361169](https://dl.acm.org/doi/abs/10.1145/3361149.3361169)

\[5\] **Use of SPADe data in IMiGEr** - L. Holy, P. Picha, R. Lipka and P. Brada, "Software Engineering Projects Analysis using Interactive Multimodal Graph Explorer - IMiGEr," 10th International Conference on Information Visualization Theory and Applications (IVAPP), 2019, pp. 330-337, https://www.scitepress.org/Papers/2019/75798.

\[6\] **AP operationalization through SPEM models** - L. Simeckova, P. Brada and P. Picha, "SPEM-Based Process Anti-Pattern Models for Detection in Project Data," 46th Euromicro Conference on Software Engineering and Advanced Applications (SEAA), 2020, pp. 89-92, [doi: 10.1109/SEAA51224.2020.00024](https://ieeexplore.ieee.org/abstract/document/9226339).

\[7\] **Dataset used for [Fire Drill](https://github.com/ReliSA/Software-process-antipatterns-catalogue/blob/master/catalogue/Fire_Drill.md) detection case study** - S. Hönel, P. Pícha, P. Brada, and L. Rychtarova, "Detection of the Fire Drill anti-pattern: Nine real-world projects with ground truth, issue-tracking data, source code density, models and code," 2021, [doi: 10.5281/zenodo.4734053](https://www.diva-portal.org/smash/record.jsf?pid=diva2%3A1548956&dswid=-6852).
