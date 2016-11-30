package nbipackage;
/**
 * Klasa sluzaca do parsowania oraz przeksztalcenia odpowiedzi serweraM2000 do listy obiektow typu Paczka
 * @author turczyt
 */
public class NPack {

    
    
    public static final int FORMAT_PIONOWY=1;
    public static final int FORMAT_POZIOMY=2;

   
    java.util.ArrayList<Paczka> wszPaczki;
    java.util.ArrayList<StacjaDane> stacjaDane;
    /**
     *  Konstruktor wykorzystywany przy formie listowania : HORRIZONTAL :( dane w formie tablicy gdzie w pierwszy wiersz to ciag nazw parametrow oddzielony tabulacjami, kazdy kolejny wiersz to pojedyncza odpowiedz zapytania )
     *
     * @param odpowiedzM2000 dane wejsciowe
     *        odpowiedz serwera M2000 w postaci nie przetworzonej
     */
     public NPack(String odpowiedzM2000,Integer typ)
    {
	if(typ==FORMAT_POZIOMY)
	{
	     java.util.ArrayList wyniktmp = new java.util.ArrayList();
	    String[] linie = odpowiedzM2000.split("\n");
	    NewFile w = new NewFile("logiNpack.txt");

	    boolean nagl = false;
	    boolean param = false;

	    boolean brakDanych = false;
	    boolean retcodezero = false;
	    boolean param1 = false;
	    java.util.ArrayList<Paczka> znaczace = wybierzZnaczaceLinie(odpowiedzM2000);//,atrybutyNaglowka);
	    wszPaczki = new java.util.ArrayList<Paczka>();
	    wszPaczki = znaczace;
	}
	else if(typ==FORMAT_PIONOWY)
	{
	    NewFile n = new NewFile();
	    String[] komenda=new String[]{"%%",":"};
	    String[] beginStacjaString=new String[]{"------"};
	    String[] endStacjaString=new String[]{"---    END"};
	    stacjaDane = new java.util.ArrayList<StacjaDane>();
	    String[] linie = odpowiedzM2000.split("\n");
	    boolean stacjaBeginB = false;
	    boolean stacjaEndB = false;
	    boolean komendaB = false;
	    boolean stacjaB = false;
        StacjaDane StacjaTmp = new StacjaDane();
        String test = "";
        String lineTmp = "";
        boolean przerwij = false;
        for (int l = 0; l < linie.length && !przerwij; l++) {
            lineTmp = linie[l];
            if (n.zawiera(linie[l], komenda)) {

                komendaB = true;

            }
            if (n.zawiera(linie[l], beginStacjaString)) {
                if (stacjaBeginB) {
                    stacjaDane.add(new StacjaDane(test));
                }
                stacjaBeginB = true;
                stacjaEndB = false;
                StacjaTmp = new StacjaDane();
                test = "";




            }
            if (n.zawiera(linie[l], new String[]{"Total", "number", "of", "all", "results"})) {
                //test=test+"\n"+linie[l];
                test = test + "\n" + endStacjaString;
                if (komendaB) {
                    stacjaDane.add(new StacjaDane(test));
                }
                przerwij = true;
            }
            if (n.zawiera(linie[l], endStacjaString)) {
                test = test + "\n" + linie[l];
                if (komendaB) {
                    stacjaDane.add(new StacjaDane(test));
                }
                stacjaEndB = true;
                stacjaBeginB = false;
                stacjaB = false;
                komendaB = false;
            }

            if (stacjaBeginB && !stacjaEndB) {
                stacjaB = true;
            }

            if (stacjaB) {
                test = test + "\n" + lineTmp;

            }

        }
    }
    }
    public NPack(String odpowiedzM2000)
    {
        java.util.ArrayList wyniktmp = new java.util.ArrayList();
        String[] linie = odpowiedzM2000.split("\n");
        NewFile w = new NewFile("logiNpack.txt");

        boolean nagl = false;
        boolean param = false;

        boolean brakDanych = false;
        boolean retcodezero = false;
        boolean param1 = false;
        java.util.ArrayList<Paczka> znaczace = wybierzZnaczaceLinie(odpowiedzM2000);//,atrybutyNaglowka);
        wszPaczki = new java.util.ArrayList<Paczka>();
        wszPaczki = znaczace;
    }
    /**
     *  Konstruktor wykorzystywany przy formie listowania : VERTICAL : kazdy zestaw parametrow (oznaczajacy pojedyncza odpowiedz ) oddzielony jest pusta linia .Parametry w zestawie znajduja sie kazdy w osobnej linie , w formacie : NAZWA PARAMETRU  =  WARTOSC PARAMETRU
     *
     * @param odpM2000 dane wejsciowe w podzielone na linie
     * @param komenda  komenda wydana na M2000
     * @param beginStacjaString string pomocniczy wyznaczajacy linie od ktorej zaczyna sie glowna czesc odpowiedzi M2000( zawierajaca nazwy parametrow wraz z wartosciami)
     * @param endStacja Stringstring pomocniczy wyznaczajacy linie koncowa glownej czesci odpowiedzi M2000( zawierajaca nazwy parametrow wraz z wartosciami) np END
     */
    public NPack(String[] odpM2000, String[] komenda, String[] beginStacjaString, String[] endStacjaString)
    {
        NewFile n = new NewFile();
        stacjaDane = new java.util.ArrayList<StacjaDane>();
        String[] linie = odpM2000;//paczka.split("\n");
        
        boolean stacjaBeginB = false;
        boolean stacjaEndB = false;
        boolean komendaB = false;
        boolean stacjaB = false;
        StacjaDane StacjaTmp = new StacjaDane();
        String test = "";
        String lineTmp = "";
        boolean przerwij = false;
        for (int l = 0; l < linie.length && !przerwij; l++)
	{
            lineTmp = linie[l];
            if (n.zawiera(linie[l], komenda))
	    {
                komendaB = true;
            }
            if (n.zawiera(linie[l], beginStacjaString))
	    {
                if (stacjaBeginB)
		{
                    stacjaDane.add(new StacjaDane(test));
                }
                stacjaBeginB = true;
                stacjaEndB = false;
                StacjaTmp = new StacjaDane();
                test = "";
            }
            if (n.zawiera(linie[l], new String[]{"Total", "number", "of", "all", "results"}))
	    {
                //test=test+"\n"+linie[l];
                test = test + "\n" + endStacjaString;
                if (komendaB)
		{
                    stacjaDane.add(new StacjaDane(test));
                }
                przerwij = true;
            }
            if (n.zawiera(linie[l], endStacjaString))
	    {
                test = test + "\n" + linie[l];
                if (komendaB)
		{
                    stacjaDane.add(new StacjaDane(test));
                }
                stacjaEndB = true;
                stacjaBeginB = false;
                stacjaB = false;
                komendaB = false;
            }

            if (stacjaBeginB && !stacjaEndB)
	    {
                stacjaB = true;
            }

            if (stacjaB)
	    {
                test = test + "\n" + lineTmp;
            }
        }
    }
    public java.util.ArrayList<String> getAtributsName(String Tresc) {
        String[] linie = Tresc.split("\n");
        int i = 0;
        String odp = "";
        NewFile w = new NewFile("logiNpack.txt");
        while (i < linie.length && !(w.zawiera(linie[i], new String[]{"------------------"}))) {
            i++;
        }
        if (i < (linie.length - 1)) {
            odp = linie[i + 1];
        }
        System.out.println("nazwy Atrybotow:\n" + splitToToken(odp).toString());
        return splitToToken(odp);


    }

    public java.util.ArrayList<String> splitToToken(String linia) {
        linia = linia.replaceAll(" ", "@");
        linia = linia.replaceAll("\t", "@@");
        linia = linia.replaceAll(System.getProperty("line.separator"), "@@");

        linia = linia.replaceAll("@@", ";");
        java.util.ArrayList<String> argList = new java.util.ArrayList<String>();
        String[] argTmp = linia.split(";");
        for (int z = 0; z < argTmp.length; z++) {
            if (argTmp[z] != null && argTmp[z].trim().length() > 0) {

                argTmp[z] = argTmp[z].replaceAll("@", " ");

                argTmp[z] = argTmp[z].trim();
                if (argTmp[z] != null && argTmp[z].length() > 0) {

                    argList.add(argTmp[z]);
                }
            }
        }
        return argList;
    }

    public int getParamLinesIndex(String Tresc) {
        String[] linie = Tresc.split("\n");
        int i = 0;
        String odp = "";
        NewFile w = new NewFile("logiNpack.txt");
        while (i < linie.length && !(w.zawiera(linie[i], new String[]{"------------------"}))) {
            i++;
        }
        if (i < (linie.length - 1)) {
            return i++;
        } else {
            return -1;
        }
    }

    public java.util.ArrayList<Paczka> wybierzZnaczaceLinie(String Tresc) {
        String[] linie = Tresc.split("\n");
        //int i=0;
        String odp = "";
        NewFile w = new NewFile("logiNpack.txt");
        boolean naglFlag = false;
        boolean endZna = false;
        java.util.ArrayList<String> Znaczace = new java.util.ArrayList<String>();
        String LiniaNaglowka = "";
        int obecI = 0;
        java.util.ArrayList<Paczka> listaPaczek = new java.util.ArrayList<Paczka>();
        java.util.ArrayList<String> nazwyParam = new java.util.ArrayList<String>();
        while (obecI < linie.length) {
            while (obecI < linie.length && !w.zawiera(linie[obecI], "------")) {
                //System.out.println("#("+obecI+")="+linie[obecI]);
                obecI++;
            }


            if (obecI < linie.length && w.zawiera(linie[obecI], "------")) {
                //System.out.println("minusy("+obecI+")="+linie[obecI]);
                obecI++;
                while (obecI < linie.length && linie[obecI].trim().equals("")) {
                    //System.out.println("linia pusta nr="+obecI);
                    obecI++;

                }
                if (obecI + 1 < linie.length) {
                    LiniaNaglowka = linie[obecI];////////NAGLOWEK
                    //System.out.println("NAGLOWEK ("+obecI+")="+LiniaNaglowka);
                    nazwyParam = splitToToken(LiniaNaglowka);

                    obecI++;
                }
            }

            while (obecI < linie.length && !endZna) {
                if (w.zawiera(linie[obecI], "Number of results")) {
                    endZna = true;
                }
                //if(w.zawiera(linie[obecI],"Number of results")
                //	endZna=true;


                if (!endZna && !linie[obecI].trim().equals("")) {///////////////linie z argumentami

                    //System.out.println(linie[obecI]);
                    //System.out.println("------------------------------------------");
                    java.util.ArrayList<String> wartosciParam = splitToToken(linie[obecI]);
                    //System.out.println("PACZKA="+linie[obecI]);
                    Paczka tmpPak = new Paczka();
                    for (int a = 0; a < nazwyParam.size(); a++) {
                        String Nazwa = nazwyParam.get(a);
                        Nazwa = Nazwa.trim();
                        String wartosc = "";
                        if (!Nazwa.equals("") && wartosciParam != null) {
                            if (a < wartosciParam.size()) {
                                wartosc = wartosciParam.get(a);
                                if (wartosc != null) {
                                    wartosc = wartosc.trim();
                                    tmpPak.dodaj(Nazwa, wartosc);
                                    //System.out.println(Nazwa+"="+wartosc);
                                }
                            }
                        }
                    }
                    Znaczace.add(linie[obecI]);
                    listaPaczek.add(tmpPak);
                }
                obecI++;
            }
            endZna = false;
            obecI++;
        }
        /*for(int i=0;i<linie.length;i++)
        {
        if(w.zawiera(linie[i],new String[]{"Number of results"}))
        {
        naglFlag=false;
        endZna=true;
        }
        if(naglFlag&&!endZna)
        {
        if(linie[i]!=null&&linie[i].length()>0&&linie[i].trim().length()>0)
        {	Znaczace.add(linie[i]);
        //System.out.println(linie[i]);
        }
        }
        if(naglowek!=null&&naglowek.size()>0)
        {
        String[] tabNaglowkowa=new String[naglowek.size()];
        for(int z=0;z<naglowek.size();z++)
        {
        if(naglowek.get(z)!=null&&naglowek.get(z).length()>0)
        tabNaglowkowa[z]=naglowek.get(z);
        else
        tabNaglowkowa[z]="";
        }
        if(linie[i]!=null&&linie[i].length()>0&&w.zawiera(linie[i],tabNaglowkowa))
        {
        naglFlag=true;
        endZna=false;
        }
        }
        }*/
        return listaPaczek;

    }   

    public java.util.ArrayList getStacje()
    {
        return this.stacjaDane;
    }

    public NPack() {
        ;
    }

    public java.util.ArrayList<Paczka> getPacks(String[] zmienne, String[] wartosci) {
        java.util.ArrayList wypluj = new java.util.ArrayList();
        for (int s = 0; s < stacjaDane.size(); s++) {
            StacjaDane st = (StacjaDane) stacjaDane.get(s);

            Paczka[] megaPack = st.getAllPacks();
            if (megaPack != null) {
                for (int p = 0; p < megaPack.length; p++) {
                    Paczka paka = megaPack[p];
                    boolean dodaj = true;
                    for (int z = 0; z < zmienne.length; z++) {
                        if (paka.getWartosc(zmienne[z]) != null && paka.getWartosc(zmienne[z]).trim().equals(wartosci[z].trim())); else {
                            dodaj = false;
                        }
                    }
                    if (dodaj) {
                        wypluj.add(paka);
                    }
                }
            }
        }
        return wypluj;
    }

    public java.util.ArrayList<Paczka> getAllPacks() {
        if (wszPaczki != null && wszPaczki.size() > 0) {
            return wszPaczki;
        }
        java.util.ArrayList wypluj = new java.util.ArrayList();
        if (stacjaDane != null) {
            for (int s = 0; s < stacjaDane.size(); s++) {
                StacjaDane st = (StacjaDane) stacjaDane.get(s);

                Paczka[] megaPack = st.getAllPacks();
                if (megaPack != null) {
                    for (int p = 0; p < megaPack.length; p++) {
                        Paczka paka = megaPack[p];
                        wypluj.add(paka);
                    }
                }
            }
        }
        return wypluj;
    }

    public java.util.ArrayList<Paczka> getAllPoziomPacks() {
        return wszPaczki;
    }

    public Paczka getPaczka(String[] zmienne, String[] wartosci) {
        java.util.ArrayList wypluj = getPacks(zmienne, wartosci);
        if (wypluj != null && wypluj.size() > 0) {
            return (Paczka) wypluj.get(0);
        } else {
            return null;
        }
    }
}