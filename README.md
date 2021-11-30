# Android Mobile Development Project
## SMN-Aggregator
https://github.com/UomMobileDevelopment/Project_2020-21

## Μέλη ομάδας
  - Παναγιώτα Τυχάλα, Α.Μ.: dai18162
  - Παύλος Μπάρκας, Α.Μ. dai18022

Στα πλαίσια της εργασίας του μαθήματος Ανάπτυξη Εφαρμογών για Κινητές Συσκευές 
αναπτύξαμε μια εφαρμογή με την οποία ο χρήστης μπορεί:
  1. Να δεί μια λίστα με trending hashtags του Twitter.
  
     ![Alt text](https://i.postimg.cc/R0MwcrJn/readme1.jpg)
     ![Alt text](https://i.postimg.cc/BvMHph27/readme2.jpg)
 
  2. Αναζητά ανάμεσα στα trending hashtags εισάγοντας κείμενο απ'το πληκτρολόγιο.
  
     ![Alt text](https://i.postimg.cc/63qRmd0W/readme3.jpg)
  
  3. Επιλέγει ένα hashtag από την προηγούμενη λίστα και βλέπει posts απ΄το twitter
     που περιέχουν το συγκεκριμένο hashtag.
     
     ![Alt text](https://i.postimg.cc/3rPS2R84/readme4.jpg)
     
  4. Επιλέγει όποιο post θέλει απ'τη λίστα και βλέπει λεπτομέρειες καθώς και 
     τα σχόλια που έχουν γίνει στο post. Επιπλέον μπορεί να κάνει like στο post.
     
     ![Alt text](https://i.postimg.cc/3ryfypTQ/readme5.jpg)
     ![Alt text](https://i.postimg.cc/QVGTCvrC/readme6.jpg)
     ![Alt text](https://i.postimg.cc/76R78fsg/readme7.jpg)
     ![Alt text](https://i.postimg.cc/gj6hYmQ1/readme8.jpg)
     
  5. Δημιουργεί νέο post με κείμενο ή/και φωτογραφία σε όποιο από τα 3 social media
     επιθυμεί. Η επιλογή του social media γίνεται σε μια συγκεκριμένη οθόνη.
     
     ![Alt text](https://i.postimg.cc/5NLPgVyP/readme9.jpg)
     ![Alt text](https://i.postimg.cc/6pKM1mGB/readme10.jpg)
     ![Alt text](https://i.postimg.cc/g2Kg9Hv8/readme11.jpg)
     ![Alt text](https://i.postimg.cc/wBfFMx4B/readme12.jpg)
     ![Alt text](https://i.postimg.cc/Ls6NmgT7/readme13.jpg)
     ![Alt text](https://i.postimg.cc/9MspP8h5/readme15.jpg)
     ![Alt text](https://i.postimg.cc/VLLGtBNK/readme16.jpg)
     
  6. Δημιουργεί νέο story με φωτογραφία στο facebook ή στο instagram. 
  
     ![Alt text](https://i.postimg.cc/XJrLCMtk/readme14.jpg)
     ![Alt text](https://i.postimg.cc/9MspP8h5/readme15.jpg)

Η εφαρμογή αναπτύχθηκε χρησιμοποιώντας το Android Studio. Για να την τρέξετε θα χρεαστεί
να προσθέσετε τα απαραίτητα Api Keys του Twitter στο gradle.properties. **Στο στάδιο ανάπτυξης
για να δημοσιεύσει ο χρήστης σε οποιοδήποτε social media θα πρέπει να είναι δηλωμένος ως 
tester στο project.**

## Libraries
  - implementation 'com.facebook.android:facebook-android-sdk:[5,6)'
  - implementation 'com.facebook.android:facebook-share:[5,6)'
  - implementation group: 'org.twitter4j', name: 'twitter4j-core', version: '4.0.7'
