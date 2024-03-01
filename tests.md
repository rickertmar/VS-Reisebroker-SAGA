Dokumentation der Serverantworten

### Test 1: Schreiben und Lesen
**Ziel:** Überprüfung der grundlegenden WRITE- und READ-Funktionen.  
**Client-Anfrage:**
```console
- WRITE-Anfrage gesendet an Server: `WRITE,test.txt,1,Hello World!`
- READ-Anfrage gesendet an Server: `READ,test.txt,1`
```
**Server-Konsolen-Log:**
```console
- Worker: Bearbeite Anfrage von /127.0.0.1 - Anfrage: WRITE,test.txt,1,Hello World!
- Worker: Bearbeite Anfrage von /127.0.0.1 - Anfrage: READ,test.txt,1
```
**Client-Konsolen-Log:**
```console
- OK
- OK, Hello World!
```
**Ausschnitt aus Datei:**
```test.txt
- Hello World
-
```

---

### Test 2: Überschreiben von Daten
**Grund und Ziel:** Testen des Überschreibens von Daten in einer bestehenden Datei.  
**Client-Anfrage:**
```console
- WRITE-Anfrage gesendet an Server: `WRITE,test.txt,1,Hello World3!`
- READ-Anfrage gesendet an Server: `READ,test.txt,1`
```
**Server-Konsolen-Log:**
```console
- Worker: Bearbeite Anfrage von /127.0.0.1 - Anfrage: WRITE,test.txt,1,Hello World3!
- Worker: Bearbeite Anfrage von /127.0.0.1 - Anfrage: READ,test.txt,1
- Reader: Warte auf Writer
```
**Client-Konsolen-Log:**
```console
- OK
- OK, Hello World3!
```
**Ausschnitt aus Datei:**
```test.txt
- Hello World3!
-
```

---

### Test 3: Falsche Zeilennummer
**Grund und Ziel:** Überprüfung des Verhaltens bei READ-Anfragen mit einer ungültigen Zeilennummer.  
**Client-Anfrage:**
```console
- WRITE-Anfrage gesendet an Server: `WRITE,test.txt,1,Hello World4!`
- READ-Anfrage gesendet an Server: `READ,test.txt,199`
```
**Server-Konsolen-Log:**
```console
- Worker: Bearbeite Anfrage von /127.0.0.1 - Anfrage: READ,test.txt,199
```
**Client-Konsolen-Log:**
```console
- ERROR Zeile exisitiert nicht
```

---

### Test 4: Erstellung einer neuen Datei
**Grund und Ziel:** Test der Erstellung einer neuen Datei durch eine WRITE-Anfrage.  
**Client-Anfrage:**
```console
- WRITE-Anfrage gesendet an Server: `WRITE,test2.txt,1,Hello World!`
- READ-Anfrage gesendet an Server: `READ,test2.txt,1`
```
**Server-Konsolen-Log:**
```console
- Worker: Bearbeite Anfrage von /127.0.0.1 - Anfrage: WRITE,test2.txt,1,Hello World!
- Worker: Bearbeite Anfrage von /127.0.0.1 - Anfrage: READ,test2.txt,1
- Reader: Warte auf Writer
```
**Ausschnitt aus Datei:**
```test2.txt
- Hello World!
-
```

---

### Test 5: Lesen einer nicht existierenden Datei
**Grund und Ziel:** Überprüfung des Serververhaltens beim Versuch, eine nicht vorhandene Datei zu lesen.  
**Client-Anfrage:**
```console
- READ-Anfrage gesendet an Server: `READ,test3.txt,1`
```
**Server-Konsolen-Log:**
```console
- Worker: Bearbeite Anfrage von /127.0.0.1 - Anfrage: READ,test3.txt,1
```
**Client-Konsolen-Log:**
```console
- ERROR Datei exisitiert nicht
```

---

### Test 6: Server unter Last
**Grund und Ziel:** Test des Serververhaltens unter Last durch eine Mehrere Anfragen gleichzeitig abwechselnd pro Thread schreiben und lesen. 
ALLE Threads in die selbe Datei um Schreiber Priorität zu zeigen


```JAVA
        for (int i = 0; i < 10; i++) {
            if (i%2==0)
                new ThreadRequest(socket, address, "READ,test4.txt,1").start();
            else
                new ThreadRequest(socket, address, "WRITE,test4.txt,1,Hello World!"+i).start();
        }
```
**Server-Konsolen-Log:**
```console
-Worker: Bearbeite Anfrage von /127.0.0.1 - Anfrage: READ,test4.txt,1
-Worker: Bearbeite Anfrage von /127.0.0.1 - Anfrage: WRITE,test4.txt,1,Hello World!1
-Worker: Bearbeite Anfrage von /127.0.0.1 - Anfrage: READ,test4.txt,1
-Worker: Bearbeite Anfrage von /127.0.0.1 - Anfrage: READ,test4.txt,1
-Reader: Warte auf Writer
-Worker: Bearbeite Anfrage von /127.0.0.1 - Anfrage: WRITE,test4.txt,1,Hello World!3
-Worker: Bearbeite Anfrage von /127.0.0.1 - Anfrage: WRITE,test4.txt,1,Hello World!5
-Worker: Bearbeite Anfrage von /127.0.0.1 - Anfrage: READ,test4.txt,1
-Reader: Warte auf Writer
-Worker: Bearbeite Anfrage von /127.0.0.1 - Anfrage: WRITE,test4.txt,1,Hello World!7
-Worker: Bearbeite Anfrage von /127.0.0.1 - Anfrage: READ,test4.txt,1
-Worker: Bearbeite Anfrage von /127.0.0.1 - Anfrage: WRITE,test4.txt,1,Hello World!9
-Reader: Warte auf Writer
```

# Notiz
"Reader: Warte auf Writer" wird nur ausgegeben wenn ein LESER warten muss.
Am Anfang mussten aber vermutlich auch Writer warten, da der Leser zu erst am Server angekommen ist.
```console
...
-Worker: Bearbeite Anfrage von /127.0.0.1 - Anfrage: READ,test4.txt,1
-Worker: Bearbeite Anfrage von /127.0.0.1 - Anfrage: WRITE,test4.txt,1,Hello World!1
...
```