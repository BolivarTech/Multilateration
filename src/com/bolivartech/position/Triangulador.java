package com.bolivartech.position;

/**
 * Copyright 2010,2011,2012 Valhala Networks C.A.<br/>
 *
 *  <p>Homepage: <a href="http://www.cuaimacrypt.com">http://www.cuaimacrypt.com</a>.</p>
 *  <p>Valhala Networks Homepage: <a href="http://www.valhalanetworks.com">http://www.valhalanetworks.com</a>.</p>
 *
 *   This Class is the Valhala Networks's Triangulador element Class.<br/><br/>
 *
 *   Esta clase implementa la definicion de un elemento triangulador para el posicionamiento en JAVA.<br/><br/><br/><br/>
 *
 *   <br/><br/>
 *
 * @author Julian Bolivar
 * @since 2007
 * @date July 22, 2012.
 * @version 1.0.0
 */
public strictfp class Triangulador extends Coordinates {
    long TimeArrival;  // Tiempo de llegada absoluto en nanosec (0 a 86.400.000) para un periodo de 24 horas
    int ID;           // Identificador del triangulador (debe ser unico)

    /**
     * Constructor con la inicializacion de posicion del triangulador
     * 
     * @param ID        ID del Triangulador (debe ser unico)
     * @param Latitude  Latitude del punto -90(S) a 90(N)
     * @param Longitude Longitud del punto -180(W) a 180(E)
     * @param Height    Altura del punto (mts) respecto al nivel del mar
     */
    public Triangulador(int ID, double Longitude, double Latitude, double Height) {
        super(Longitude,Latitude,Height);
        this.ID = ID;
    }

    /**
     * Constructor con la inicializacion de posicion del triangulador asi como
     * del Tiempo de Llegada.
     * 
     * @param ID          ID del Triangulador (debe ser unico)
     * @param Latitude    Latitude del punto -90(S) a 90(N)
     * @param Longitude   Longitud del punto -180(W) a 180(E)
     * @param Height      Altura del punto (mts) respecto al nivel del mar
     * @param TimeArrival Tiempo de llegada absoluto en nanosec (0 a 86400000) para un periodo de 24 horas
     */
    public Triangulador(int ID, double Longitude, double Latitude, double Height, long TimeArrival) {
        super(Longitude,Latitude,Height);
        this.ID = ID;
        this.TimeArrival = TimeArrival;
    }
    
    /**
     * Constructor de copiado
     * 
     * @param B Objeto Triangulador a copiar
     */
    public Triangulador(Triangulador B) {
        super(B.Longitude,B.Latitude,B.Height);
        this.ID = B.ID;
        this.TimeArrival = B.TimeArrival;
    }

    /**
     * Retorna el ID del Triangulador
     * 
     * @return ID del Triangulador
     */
    public int getID() {
        return ID;
    }

    /**
     * Establece el ID del Triangulador (debe ser unico)
     * 
     * @param ID ID del Triangulador
     */
    public void setID(int ID) {
        this.ID = ID;
    }

    /**
     * Retorna el tiempo de llegada absoluto en nanosec (0 a 86400000) para un periodo de 24 horas
     * 
     * @return Tiempo de llegada absoluto en nanosec para un periodo de 24 horas
     */
    public long getTimeArrival() {
        return TimeArrival;
    }

    /**
     * Establece el tiempo de llegada absoluto en nanosec (0 a 86400000) para un periodo de 24 horas
     * 
     * @param TimeArrival Tiempo de llegada absoluto en nanosec para un periodo de 24 horas
     */
    public void setTimeArrival(long TimeArrival) {
        this.TimeArrival = TimeArrival;
    }
    
    /**
     * Retorna la diferencia de tiempos de llegada (nanosec) del Triangulador "B" respecto a 
     * este triangulador.
     * 
     * @param B Triangulador 
     * @return  Diferencia de tiempo en nanosegundos
     */
    public double TimeDiff(Triangulador B){
        double TimeDiff;  // Diferencia de tiempos en segundos
        long TDff;
        
        // La diferencia de tiempos en la llegada de las señales
        TDff = B.TimeArrival-this.TimeArrival;
        TimeDiff = (double)TDff;
        return TimeDiff;
    }
    
    /**
     * Retorna la diferencia de Latitud (grados) del Triangulador "B" respecto a 
     * este triangulador.
     * 
     * @param B Triangulador 
     * @return  Diferencia de Latitud en grados
     */
    public double LatitudeDiff(Triangulador B){
        
        return B.Latitude - this.Latitude;
    }
    
    /**
     * Retorna la diferencia lineal de Latitud (mts) del Triangulador "B" respecto a 
     * este triangulador.
     * 
     * @param B Triangulador 
     * @return  Diferencia lineal de Latitud en mts.
     */
    public double LatitudeLinealDiff(Triangulador B){
        double LinealDiff,Radio;
        
        Radio = this.RadioMedio(this.Latitude,this.Latitude);
        LinealDiff = (B.Latitude - this.Latitude)*Math.PI/180;
        LinealDiff *= Radio;
        return LinealDiff;
    }
    
    /**
     * Retorna la diferencia angular de Latitud (grados) dada una diferencia lineal de latitud respecto a 
     * este triangulador.
     * 
     * @param DifLatitude diferencia lineal en mts. 
     * @return  Diferencia angular de Latitud en grados.
     */
    public double Lineal2AngLatitudeDiff(double DifLatitude){
        double AngDiff;
        
        AngDiff = DifLatitude/this.RadioMedio(this.Latitude,this.Latitude);
        return (AngDiff*180)/Math.PI;
    }
    
    /**
     * Retorna la diferencia de Longitud (grados) del Triangulador "B" respecto a 
     * este triangulador.
     * 
     * @param B Triangulador 
     * @return  Diferencia de Longitud en grados
     */
    public double LongitudeDiff(Triangulador B){
        
        return B.Longitude - this.Longitude;
    }

    /**
     * Retorna la diferencia lineal de Longitud (mts) del Triangulador "B" respecto a 
     * este triangulador.
     * 
     * @param B Triangulador 
     * @return  Diferencia lineal de Longitud en mts.
     */
    public double LongitudeLinealDiff(Triangulador B){
        double LinealDiff,Radio;
        
        Radio=this.RadioMedio(this.Latitude,this.Latitude);
        LinealDiff =(B.Longitude - this.Longitude)*Math.PI/180;
        LinealDiff *= Radio;
        return LinealDiff;
    }
    
    /**
     * Retorna la diferencia angular de Longitud (grados) dada una diferencia lineal de longitud respecto a 
     * este triangulador.
     * 
     * @param DifLongitude diferencia lineal en mts. 
     * @return  Diferencia angular de Longitud en grados.
     */
    public double Lineal2AngLongitudeDiff(double DifLongitude){
        double AngDiff;
        
        AngDiff = DifLongitude/this.RadioMedio(this.Latitude,this.Latitude);
        return (AngDiff*180)/Math.PI;
    }
    
    /**
     * Retorna la diferencia de Alturas (mts) del Triangulador "B" respecto a 
     * este triangulador.
     * 
     * @param B Triangulador 
     * @return  Diferencia de Alturas en metros
     */
    public double HeightDiff(Triangulador B){
        
        return B.Height - this.Height;
    }
    
    
    /**
     * Ordena el triangulador por el tiempo de arrival
     * 
     * @return TimeArrival 
     */
    @Override
    public long Metrica() {
        return TimeArrival;
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Oredena por ID, -1 si es menor que "Other", 0 si es igual a "Other"
     * 1 si es mayor que "Other"
     * 
     * @param Other
     * @return 
     */
    //@Override
    public int Order(Triangulador Other) {
        int Salida;
        
        Salida = 0;
        if(this.ID<Other.ID){
            Salida = -1;
        } else if(this.ID>Other.ID){
            Salida = 1;
        }
        return Salida;
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
