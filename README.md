# Opis:
The application is used to send data about paid (or partially paid) transactions from the ERP system Subiekt GT to the Baselinker system. When the application is running, it searches the Subiekt GT database at five-minute intervals for receivables from the last 60 days where any payment has been registered. It then compares the retrieved set of records with records stored in the auxiliary table. If it finds differences, it processes new payments and updates them through the Baselinker system's API.

For the application to function correctly, it requires passing the Baselinker order ID to the `Remarks` field in the FS documents in Subiekt GT. The ID should be passed in the following format: `BLID:<order_id>`; so that the parser can locate it among other data that often ends up in this field. This will likely require additional configuration of the application used to retrieve orders.
Installation:

The current version of the application requires the following steps:

1. Creating a table in the Subiekt GT database named `dbo.__ledu.PaidInvoices` with the following structure:

| Field | Type | Null | Key | Default | Extra |
|---|---|---|---|---|---|
| id | int | NO | PRI | NULL | auto_increment |
| nzf_NumerPelny | varchar(50) | NO |  | NULL |  |
| nzf_BLID | varchar(50)  | NO   |     | NULL    |                |
| nzf_WartoscWaluta | money        | NO   |     | NULL    |                |
| nzf_WartoscWalutaPierwotna| money        | NO   |     | NULL    |                |
| nzf_Data    | datetime     | NO   |     | NULL    |                |
| nzf_Updated | bit          | NO   |     | NULL    |                |



2. After compiling move the jar files from the repository folder to the following folder C:\BLinvoiceSync
3. Create a subflder in the main application falder and name it "logs"
4. Create a file `config.xml` in the main application folder, where all the authorization data will be stored. Example file might look like this:
 ```
   <?xml version="1.0" encoding="UTF-8"?>
   <Configuration>
   <database>
    <url>
    jdbc:sqlserver://DB_ADDRESS;database=MY_DB;encrypt=true;trustServerCertificate=true
    </url>
    <login>MY_LOGIN</login>
    <password>MY_PASSWORD</password>
   </database>
   <api>
    <token>MY_TOKEN</token>
   </api>
   </Configuration>
```
6. After launching the BLinvoiceSync.jar file the application should already work, however it is better to create a new Windows service that will handle the application life cycle. To achieve that I recommend using a wrapper, like WinSW - you can find required files and instruction how to use it here ttps://github.com/winsw/winsw
This application was not meant to be used on Linux operating system, however to my knowledge it should still work fine on Linux.

The project uses JDK 21
