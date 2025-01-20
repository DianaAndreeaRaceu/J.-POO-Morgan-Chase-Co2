	
	  Acest proiect implementeaza un sistem bancar complet, structurat modular, 
	care utilizeaza principiile programarii orientate pe obiecte (OOP). 
	Functionalitatile includ gestionarea utilizatorilor, a conturilor bancare, 
	a cardurilor, a tranzactiilor, a rapoartelor si a conversiilor valutare.
	Proiectul integreaza mai multe design patterns importante, precum Factory 
	Method, Strategy, Template Method, Composite si altele.
	
	
	A.Clase Principale:
	
	
	1. Account
		
	  Clasa abstracta Account defineste structura de baza pentru conturile bancare 
	si este extinsa de clasele Classic si Savings.

	Campuri private:
	
		iban (String): IBAN-ul contului.
		
		alias (String): Alias-ul contului.
		
		currency (String): Moneda contului.
		
		accountType (String): Tipul contului (classic sau savings).
		
		balance (double): Balanta curenta.
		
		minBalance (double): Limita minima a balantei.
		
		cards (ArrayList<Card>): Lista cardurilor asociate.
		
		transactions (ArrayList<Transaction>): Lista tranzactiilor asociate.
		
	Metode principale:
	
		addTransaction(): Adauga o tranzactie.
		
		addCard(): Adauga un card asociat.
		
		addFunds(): Creste balanta cu o suma specificata.
		
		sendMoney(): Transfera bani intre conturi.
		
		spendingsReport(): Metoda abstracta pentru generarea rapoartelor de cheltuieli.
		
		
	2. Classic (Extensie a clasei Account)
	
	
	Reprezinta un cont bancar simplu, fara dobanda.

	Metode implementate:

		-spendingsReport(): Genereaza raportul de cheltuieli pentru 
		acest tip de cont.	
		
		
	3. Savings (Extensie a clasei Account)
	
	
	Reprezinta un cont de economii care ofera dobanda.

	Contine:

	-Campuri private:
		-interestRate (double): Rata dobanzii.

	-Metode implementate:

		-spendingsReport(): Returneaza o eroare deoarece raportul 
		nu este aplicabil conturilor de economii.
		
		
	4. Card
	
	Clasa abstracta Card defineste functionalitatea de baza a unui card bancar.

	Contine:

	-Campuri private:
	
		-cardNumber (String): Numarul cardului.
		
		-active, frozen, warning (boolean): Starea cardului.
		
	-Metode abstracte:

		-payOnline(): Gestioneaza platile online.
		
		
		
	5. ClassicCard (Extensie a clasei Card)
	
	Un card standard folosit pentru plati online.

	Metode implementate:

		-payOnline(): Scade suma din balanta contului asociat si
		inregistreaza tranzactia.
		
		
		
	6. OneTimeCard (Extensie a clasei Card)
	
	Un card de unica folosinta.

	-Metode implementate:

		-payOnline(): Realizeaza plata online, apoi distruge 
		cardul si creeaza unul nou.
		
		
	7. User
	
	Reprezinta utilizatorii sistemului bancar.

	Contine:

	-Campuri private:
		-firstName, lastName, email, birthDate (String): Detalii despre 
		utilizator.
		
		-accounts (ArrayList<Account>): Lista conturilor 
		asociate utilizatorului.
		
	-Metode principale:

		-addAccount(): Adauga un cont la lista utilizatorului.
		
		-findAccountForCurrentUser(): Cauta un cont dupa IBAN.
		
		-findAccountForCurrentUserAlias(): Cauta un cont dupa alias.



	8. Bank
	
	Clasa care administreaza utilizatorii, tranzactiile si conversiile valutare.

	Contine:

	-Campuri private:
		-users (ArrayList<User>): Lista utilizatorilor inregistrati.
		
		-transactions (Transactions): Obiect pentru gestionarea 
		tranzactiilor.
		
		-currencyConverter (Converter): Obiect care gestioneaza 
		conversia valutara.
		
		
	-Metode principale:

		-addUser(): Adauga un utilizator nou.
		-findUser(): Gaseste un utilizator dupa email.
		
		
	
	9. Transactions
	
	Clasa care implementeaza logica pentru gestionarea tranzactiilor.

	-Metode principale:

		-addAccount(): Creeaza un cont pentru un utilizator.
		
		-deleteAccount(): Sterge un cont.
		
		-payOnline(): Realizeaza o plata online.
		
		-sendMoney(): Transfera bani intre conturi.
		
		-splitPayment(): Imparte o plata intre mai multe conturi.
		
		
	
	10. Converter
	
	Implementeaza Strategy Pattern pentru gestionarea conversiilor valutare.

	Contine:

	-Strategii:
		-DirectConversionStrategy: Conversie directa intre valute.
		
		-MultiStepConversionStrategy: Conversie in mai multi pasi.
	
	-Metoda principala: -convert(): Efectueaza conversia valutara folosind 
				        strategia selectata.
				        
			       

	B. Design Patterns Utilizate
	
		-Factory Method:
	
			Folosit pentru crearea obiectelor, implementat in:
				-CommandFactory (pentru comenzi).
				-AccountFactory si CardFactory (pentru conturi si carduri).
		
		-Strategy Pattern:
		
			Implementat in clasa Converter pentru gestionarea conversiilor valutare.
			Utilizat si in CashbackStrategy, cu implementari pentru reguli de cashback:
				-NrOfTransactions
				-SpendingTreshold.
		
		-Template Method:
		
			Implementat in clasele abstracte:
				-Card (pentru metodele de plata).
				-Transaction (pentru afisarea rapoartelor).
		
		-Composite Pattern:
			
			Relatia ierarhica intre User -> Account -> Card:
			Un utilizator are mai multe conturi, fiecare cu mai multe carduri.
		
		-Builder Pattern:
			
			Observabil in clasa Json, pentru construirea nodurilor JSON utilizand 
			fluent API-ul oferit de ObjectMapper.

		-Observer Pattern (Partial):
		
			Actualizarea starii conturilor si a planurilor utilizatorilor in 
			functie de tranzactii.
