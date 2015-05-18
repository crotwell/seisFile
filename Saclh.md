# Introduction #

saclh is an example client that prints the header of a SAC file.


# Example #

An example is in [src/main/java/edu/sc/seis/seisFile/sac/ListHeader.java](http://code.google.com/p/seisfile/source/browse/src/main/java/edu/sc/seis/seisFile/sac/ListHeader.java) which can also be run as a client. For example:
```
bin/saclh test.sac

test.sac
--------

     delta =        1    depmin =        0    depmax =        1     scale =   -12345    odelta =   -12345
         b =        0         e =       99         o =   -12345         a =   -12345       fmt =   -12345
        t0 =   -12345        t1 =   -12345        t2 =   -12345        t3 =   -12345        t4 =   -12345
        t5 =   -12345        t6 =   -12345        t7 =   -12345        t8 =   -12345        t9 =   -12345
         f =   -12345     resp0 =   -12345     resp1 =   -12345     resp2 =   -12345     resp3 =   -12345
     resp4 =   -12345     resp5 =   -12345     resp6 =   -12345     resp7 =   -12345     resp8 =   -12345
     resp9 =   -12345      stla =   -12345      stlo =   -12345      stel =   -12345      stdp =   -12345
      evla =   -12345      evlo =   -12345      evel =   -12345      evdp =   -12345       mag =   -12345
     user0 =   -12345     user1 =   -12345     user2 =   -12345     user3 =   -12345     user4 =   -12345
     user5 =   -12345     user6 =   -12345     user7 =   -12345     user8 =   -12345     user9 =   -12345
      dist =   -12345        az =   -12345       baz =   -12345     gcarc =   -12345        sb =   -12345
    sdelta =   -12345    depmen =     0.01     cmpaz =   -12345    cmpinc =   -12345  xminimum =   -12345
  xmaximum =   -12345  yminimum =   -12345  ymaximum =   -12345   unused6 =   -12345   unused7 =   -12345
   unused8 =   -12345   unused9 =   -12345  unused10 =   -12345  unused11 =   -12345  unused12 =   -12345
    nzyear =   -12345    nzjday =   -12345    nzhour =   -12345     nzmin =   -12345     nzsec =   -12345
    nzmsec =   -12345     nvhdr =        6     norid =   -12345     nevid =   -12345      npts =      100
    nsnpts =   -12345     nwfid =   -12345    nxsize =   -12345    nysize =   -12345  unused15 =   -12345
    iftype =        1      idep =   -12345    iztype =   -12345  unused16 =   -12345     iinst =   -12345
    istreg =   -12345    ievreg =   -12345    ievtyp =   -12345     iqual =   -12345    isynth =   -12345
   imagtyp =   -12345   imagsrc =   -12345  unused19 =   -12345  unused20 =   -12345  unused21 =   -12345
  unused22 =   -12345  unused23 =   -12345  unused24 =   -12345  unused25 =   -12345  unused26 =   -12345
     leven =        1    lpspol =        0    lovrok =        1    lcalda =        1  unused27 =        0
     kstnm =   -12345       kevnm =                FUNCGEN: IMPULSE       khole =   -12345  
        ko =   -12345       ka =  =   -12345         kt0 =   -12345         kt1 =   -12345  
       kt2 =   -12345      kt3 =  =   -12345         kt4 =   -12345         kt5 =   -12345  
       kt6 =   -12345      kt7 =  =   -12345         kt8 =   -12345         kt9 =   -12345  
        kf =   -12345   kuser0 =  =   -12345      kuser1 =   -12345      kuser2 =   -12345  
    kcmpnm =   -12345   knetwk =  =   -12345      kdatrd =   -12345       kinst =   -12345  
```

and the usage is:
```
bin/saclh filename.sac
```


