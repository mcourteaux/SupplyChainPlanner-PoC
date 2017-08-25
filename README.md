Supply Chain Planner: PoC
-------------------------

Usage:

 1. Install PostgreSQL and create a database named "poc".
 2. Run the SQL schema dump:

        psql -f sql/db_schema.sql

 3. Download the dependency project and open in NetBeans and "Build" it:

        https://github.com/mcourteaux/SupplyChainPlanner

 4. Open this project in NetBeans (do not compile).
 5. Generate the dummy data by opening the Main file:

        com/martijncourteaux/supplychainplanner/poc/dummydatagenerator/Main.java

    and run it using right-click -> Run File. Within the console, you will
    be promted to type 'yes' if you are sure you want to drop the database and
    regenerate it.
 6. Now you can run the test program:

        src/test/java/PoCTransportFilterTest.java

    Right-click -> "Test File".
 7. Feel free to toy around with different options within the test file.
    For example: Reject a agent (subcontractor) used the initial solution of the
    first test run, and regenerate the solutions.


