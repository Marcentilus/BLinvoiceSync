#Opis:
aplikacja służy do wysyłania danych o opłaconych(lub częściowo opłaconych) transakcjach z systemu ERP Subiekt GT do systemu Baselinker. Gdy aplikacja jest uruchomiona w interwałach pięciominutorych przeszukuje bazę danych Subiekta GT w poszukiwaniu należności z ostatnich 60 dni, w których zarejestrowano jakąkolwiek wpłatę, następnie porównuje otrzymany zbiór rekordów z rekordami
zapisanymi w tabeli pomocniczej. Jeśli odnajduje różnice przetwarza nowe płatności i aktualizuje je poprzez API systemu Baselinker.
#Instalacja:
Aktualna wersja aplikacji wymaga następujących kroków:
1. Stworzenie w bazie danych Subiekta GT tabeli o nazwie <dbo.__ledu.PaidInvoices> i następującej strukturze:
+---------------------------+--------------+------+-----+---------+----------------+
| Field                     | Type         | Null | Key | Default | Extra          |
+---------------------------+--------------+------+-----+---------+----------------+
| id                        | int          | NO   | PRI | NULL    | auto_increment |
| nzf_NumerPelny            | varchar(50)  | NO   |     | NULL    |                |
| nzf_BLID                  | varchar(50)  | NO   |     | NULL    |                |
| nzf_WartoscWaluta         | money        | NO   |     | NULL    |                |
| nzf_WartoscWalutaPierwotna| money        | NO   |     | NULL    |                |
| nzf_Data                  | datetime     | NO   |     | NULL    |                |
| nzf_Updated               | bit          | NO   |     | NULL    |                |
+------------------+--------------+------+-----+---------+-------------------------+

2. Po skompilowaniu projektu, trzeba przenieść pliki jar z folderu repozytorium, do folderu o ścieżce: C:\BLinvoiceSync
3. Utworzyć w folderze aplikacji podfolder o nazwie logs
4. Po uruchomieniu pliku jar BLinvoiceSync aplikacja będzie już działać, ale lepiej stworzyć usługę w systmiemie Windows, która będzie ją obsługiwać - w tym celu polecam skorzystać z wrappera WinSW, pliki oraz instrukcje można znaleźć tutaj: https://github.com/winsw/winsw. Aplikacja nie była robiona z myślą o pracy na systemie Linux, ale powinna również na nim działać.

Aplikacja wymaga zainstalowanej Javy w wersji 21