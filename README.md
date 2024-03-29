# SPADe
Software development process and project management _anti-patterns (AP)_ are known, reoccurring, bad solutions to common problems in their respective domains. AP descriptions exist almost exclusively in textual form for human consumption, rendering detection in projects difficult at best. _Application Lifecycle Management (ALM) tools_ capture day-to-day reality of software development projects as a byproduct of their use.

Our **Software Process Anti-pattern Detector (SPADe)** automates the detection of the aforementioned APs, defined in the _[Software Process Anti-pattern Catalogue (SPAC)](https://github.com/ReliSA/Software-process-antipatterns-catalogue)_, in project ALM tool data, extracted by the _Project Mining and Storage (ProMiS)_ tool.

Data from various ALM tools are mined by _data pumps_ and saved in a _database_ with a uniform _metamodel_ [1]. Gathered AP descriptions from literature are condensed in to catalogue _entries_ using a custom description _template_ [2], then _operationalized_ [3] into _models_ over project data and detected by SPADe. Results are shown in the _[Software Process Anti-pattern Web Interface (SPAWn)](https://github.com/ReliSA/SPADe-Web-GUI)_.

Both data mined, and anti-pattern language constructed from SPAC can also be exported for further analysis in DOT or JSON format, e.g., to _[Interactive Multimodal Graph Explorer (IMiGEr)](https://github.com/ReliSA/IMiGEr)_.

![Toolset overall architecture](doc/architecture/tool-new-simple.png)

## Further information
See the [ReliSA research group website](http://relisa.kiv.zcu.cz/areas/) for more context.

## Published work on SPADe related research:

\[1\] **ProMiS Metamodel** - P. Pícha and P. Brada, "ALM Tool Data Usage in Software Process Metamodeling," 2016 42th Euromicro Conference on Software Engineering and Advanced Applications (SEAA), 2016, pp. 1-8, [doi: 10.1109/SEAA.2016.37](https://ieeexplore.ieee.org/abstract/document/7592768).

\[2\] **Use of ALM data for different analysis** - P. Pícha, P. Brada, R. Ramsauer and W. Mauerer, "Towards Architect’s Activity Detection through a Common Model for Project Pattern Analysis," 2017 IEEE International Conference on Software Architecture Workshops (ICSAW), 2017, pp. 175-178, doi: [10.1109/ICSAW.2017.46](https://ieeexplore.ieee.org/abstract/document/7958478).

\[3\] **SPAC** - P. Brada and P. Picha, "Software process anti-patterns catalogue," 24th European Conference on Pattern Languages of Programs (EuroPLop), 2019, pp. 1–10, [doi: 10.1145/3361149.3361178](https://dl.acm.org/doi/abs/10.1145/3361149.3361178)

\[4\] **AP operationalization and detection approach** - P. Picha and P. Brada, "Software process anti-pattern detection in project data," 24th European Conference on Pattern Languages of Programs (EuroPLop), 2019, pp. 1–12, doi: [10.1145/3361149.3361169](https://dl.acm.org/doi/abs/10.1145/3361149.3361169)

\[5\] **Use of SPADe data in IMiGEr** - L. Holy, P. Picha, R. Lipka and P. Brada, "Software Engineering Projects Analysis using Interactive Multimodal Graph Explorer - IMiGEr," 10th International Conference on Information Visualization Theory and Applications (IVAPP), 2019, pp. 330-337, https://www.scitepress.org/Papers/2019/75798.

\[6\] **AP operationalization through SPEM models** - L. Simeckova, P. Brada and P. Picha, "SPEM-Based Process Anti-Pattern Models for Detection in Project Data," 46th Euromicro Conference on Software Engineering and Advanced Applications (SEAA), 2020, pp. 89-92, [doi: 10.1109/SEAA51224.2020.00024](https://ieeexplore.ieee.org/abstract/document/9226339).

\[7\] **Dataset used for [Fire Drill](https://github.com/ReliSA/Software-process-antipatterns-catalogue/blob/master/catalogue/Fire_Drill.md) detection case study** - S. Hönel, P. Pícha, P. Brada, and L. Rychtarova, "Detection of the Fire Drill anti-pattern: Nine real-world projects with ground truth, issue-tracking data, source code density, models and code," 2021, [doi: 10.5281/zenodo.4734053](https://www.diva-portal.org/smash/record.jsf?pid=diva2%3A1548956&dswid=-6852).
