# Zadanie Rekrutacyjne Smart4Aviation

Linie lotnicze S4A Airlines niedawno rozpoczęły obsługę nowych tras. Zgodnie z wizjonerską
strategią prezesa Mika Smartlarka, samoloty są przypisane do obsługi n tras, oznaczonych
liczbami od 1 do n. Każdy samolot obsługuje jedną trasę i ma przypisaną liczbę pasażerów jaką
maksymalnie można przewieźć. Może się ona jednak zmieniać na przykład z powodu
uszkodzeń które wpływają na masę startową samolotu.

Prezes Smartlark obiecał premie za najlepszy zestaw tras obsługiwanych przez samoloty w
jednym przedziale. Kryteria oceny pozostają tajemnicą, jednak jednym z nich jest zsumowanie
całkowitej liczby dostępnych miejsc od pierwszego dnia pomiarów do chwili 0 ≤ t ≤10^11 przez
samoloty przypisane obecnie do tras w przedziałach wybranych przez prezesa.

Linia lotnicza może w dowolnej chwili wycofać dowolny samolot z trasy lub przypisać nowy
samolot do wolnej trasy. Ponadto, w wyniku różnych okoliczności (np. zmiany floty),
maksymalna liczba pasażerów przewożonych przez dany samolot może ulec zmianie. Pomóż
prezesowi obliczyć liczbę dostępnym miejsc w samolotach na wybranych przedziałach tras do
danej chwili.
Dane pobieramy ze standardowego wyjścia i zwracamy na standardowe wyjście.

## Wejście

W pierwszym wierszu wejścia znajdują się dwie liczby całkowite 1≤ n ≤ 10^7 , 1≤ q ≤ 10^7
oznaczające liczbę tras i liczbę zapytań. W dniu t=0 każdy samolot jest przypisany do dokładnie
jednej trasy.
W drugim wierszu znajduje się n liczb całkowitych 0 ≤ pi ≤ 1000, oznaczających maksymalną
liczbę pasażerów przewożonych przez i-ty samolot.
W następnych q wierszach znajdują się uporządkowane chronologicznie zapytania postaci:

* P i p t – zmień maksymalną liczbę pasażerów przewożonych przez samolot i na p w
dniu t.
*  C i t – wycofaj samolot i z trasy od dnia t - nie uwzględniamy go w sumowaniu.
* A i p t – przypisz samolot i do nowej trasy z maksymalną liczbą pasażerów p w dniu
t.
*  Q i j t – dla samolotów aktywnych w dniu t, wypisz sumę liczby dostępnych miejsc
dla pasażerów w samolotach przypisanych do tras od i do j do dnia t. (Jeśli wcześniej tą
trasę obsługiwała inna maszyna to danych dla niej już nie uwzględniamy)

## Wyjście
 
Wypisz wszystkie odpowiedzi na zapytania typu Q – po jednej w każdym wierszu.
Gwarantowane jest, że pojawi się przynajmniej jedno takie zapytanie.
Rozwiązanie będzie oceniane nie tylko ze względu na poprawność zwracanych danych, ale też
na wydajność przetwarzania. Oczekujemy rozwiązania w Javie.

### Przykład 1:
Wejście
~~~
5 7
1 2 3 2 4
Q 1 5 2
Q 2 3 2
C 2 3
P 3 5 3
Q 2 4 4
A 2 5 6
Q 1 5 8
~~~

Wyjście:
~~~
24
10
22
100
~~~

### Przykład 2:

Wejście
~~~
1 7
2
Q 1 1 1
C 1 1
A 1 6 2
Q 1 1 3
Q 1 1 4
Q 1 1 7
Q 1 1 8
~~~

Wyjście:
~~~
2
6
12
30
36
~~~