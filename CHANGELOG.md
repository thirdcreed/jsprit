Change-log
==========
**v0.0.5** @ 2013-11-22

- more memory-efficient RadialRuin (issue #53)
- bug fix (issue #51)

**v0.0.4** @ 2013-10-17

- a number of internal improvements
- license change from GPLv2 to LGPLv3
- add premature algorithm termination: PrematureAlgorithmBreaker.java and its implementations
- searchStrategy.java: public SearchStrategy(SolutionSelector,SolutionAcceptor) --> public SearchStratgy(SolutionSelector,SolutionAcceptor,SolutionCostCalculator)
- searchStrategy.java: public boolean run(...) --> public DiscoveredSolution run(...)
- vehicleImpl.VehicleType.Builder --> VehicleTypeImpl.Builder
- vehicleImpl.VehicleBuilder --> VehicleImpl.Builder

**v0.0.3** @ 2013-06-04

- bug fix - access resources in jar

**v0.0.2** @ 2013-06-03

- bug fix - access resources in jar

**v0.0.1** @ 2013-06-02
