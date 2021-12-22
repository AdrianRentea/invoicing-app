Aceasta aplicatie are ca scop generarea automata a facturilor pentru toate companiile aflate in directorul suppliers.

Facturile sunt generate pe baza template-ului "Template invoice_test- 022021.xlsx" din directorul suppliers. Toate facturile generate sunt in format xlsx si PDF.

Aplicatia scaneaza periodic directorul suppliers si stie sa determine automat daca se adauga o companie noua (un director nou sub directorul suppliers)  cu un suppier nou, unul sau mai multi customers si un invoice controller

Pentru ca aplicatia sa functioneze cum trebuie este necesar ca structura directoarelor sa se respecte , mai exact fiecare companie sa contina fisierul supplier , cel putin un fisier customer si fisierul invoice controller.

La rulare, aplicatia genereaza automat pentru o companie,  directorul generatedInvoices si toate directoarele copil al acestuia.


Deoarece aplicatia scaneaza periodic directorul "suppliers", exista un mecanism  ce nu permite generarea duplicata a unei facturi pentru o luna.


Terminologii folosite:

supplier - companie care presteaza serviciile , drept urmare este compania care factureaza

customer - compania careia ii sunt prestate serviciile, drept urmare este compania careia i se factureaza

Invoice controller - fisier in care este tinuta evidenta tuturor facturilor emise de catre supplier, pe baza acestui fisier, aplicatia stie sa genereze corect numarul si seria pentru noile facturi.

Fiecare copil direct al directorului "suppliers" , reprezinta o companie separata care emite facturi, drept urmare are supplier, customers si invoice controller propriu

Facturile generate de aplicatie se vor salva in directorul fiecarei companii in structura /generatedInvoices/MMYYYY/ si vor avea ca nume invoice_{nume customer}.xlsx pentru fisierul in format xlsx si nume invoice_{nume customer}.pdf pentru fisierul in format pdf.


