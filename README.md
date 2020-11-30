== Per eseguire il progetto dare il comando:
----
	mvn compile spring-boot:run
----
Se non funziona o si ottiene un errore tipo:
no sapjco3 in java.library.path 
Scaricare nuovamente la libreria sapjco3 dal sito di Sap: 'https://support.sap.com/en/product/connectors/jco.html'
ed inserire sia il .jar che il .dll nella cartella src\main\resources

--
bisogna poter raggiungere l'ip della macchina dove si trova installato Sap