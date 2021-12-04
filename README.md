Aceasta aplicatie are ca scop generarea automata a facturilor pentru toate companiile aflate in directorul furnizori.

Facturile sunt generate pe baza template-ului "Template invoice_test- 022021.xlsx" din directorul furnizori. Toate facturile generate sunt in format xlsx

Aplicatia scaneaza periodic directorul furnizori si stie sa determine automat daca se adauga o companie noua (un director nou sub directorul furnizori) ce are un supplier nou, unul sau mai multi customers si un invoice controller

Pentru ca aplicatia sa functioneze cum trebuie este necesar ca structura directoarelor sa se respecte , mai exact fiecare companie sa contina fisierul supplier , cel putin un fisier customer si fisierul invoice controller.

La rulare, aplicatia genereaza automat pentru o companie,  directorul generatedInvoices si toate directoarele copil al acestuia.


Deoarece aplicatia scaneaza periodic directorul "furnizori", exista un mecanism  care nu permite generarea unei facturi ce este deja generata.


Terminologii folosite:

supplier - companie care presteaza serviciile , drept urmare este compania care factureaza

customer - compania careia ii sunt prestate serviciile, drept urmare este compania careia i se factureaza

Invoice controller - fisier in care este tinuta evidenta tuturor facturilor emise de catre supplier, pe baza acestui fisier, aplicatia stie sa genereze corect numarul si seria noilor facturi

Fiecare copil direct al directorului "furnizor" , reprezinta o companie separata care emite facturi, drept urmare are supplier, customers si invoice controller propriu

Facturile generate de aplicatie se vor salva in directorul fiecarei companii in structura /generatedInvoices/MMYYYY/ si vor avea ca nume invoice_{nume customer}.xlsx


