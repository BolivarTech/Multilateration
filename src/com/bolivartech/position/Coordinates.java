package com.bolivartech.position;

import com.bolivartech.utils.exception.UtilsException;
import com.bolivartech.utils.log.LoggerManager;
import com.bolivartech.utils.numericalalgorith.NAFunction;
import com.bolivartech.utils.numericalalgorith.NewtonRaphson;
import com.bolivartech.utils.sort.Sortable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Copyright 2010,2011,2012 Valhala Networks C.A.<br/>
 *
 * <p>Homepage: <a
 * href="http://www.cuaimacrypt.com">http://www.cuaimacrypt.com</a>.</p>
 * <p>Valhala Networks Homepage: <a
 * href="http://www.valhalanetworks.com">http://www.valhalanetworks.com</a>.</p>
 *
 * This Class is the Valhala Networks's Coordinates element Class.<br/><br/>
 *
 * Esta clase implementa la definicion de una coordenada para la ubicacion
 * geografica en la tierra.<br/><br/><br/><br/>
 *
 * <br/><br/>
 *
 * @author Julian Bolivar
 * @since 2007 @date July 22, 2012.
 * @version 1.0.0
 */
public strictfp class Coordinates extends Sortable {

    double Latitude;  // Latitude del punto -90(S) a 90(N)
    double Longitude; // Longitud del punto -180(W) a 180(E)
    double Height;    // Altura del punto en (mts) respecto al nivel del mar
    
    LoggerManager Bitacora;
    
    /** Radio ecuatorial de la tierra (m) */
    public final static double RadioEquatorial = 6378136.3;
    /** Radio polar de la tierra (m) */
    public final static double RadioPolar = 6356753.0;
    /** Aceleracion de gravedad polar (m/s^2) */
    public final static double PolarGravity = 9.832186;
    /** Aceleracion de gravedad ecuatorial (m/s^2) */
    public final static double EquatorialGravity = 9.780328;
    
    /**
     * Constructo por defecto de las coordenadas<br/><br/>
     *
     * Establece las coordenadas en el punto<br/><br/> 
     * Latitud = 0<br/> 
     * Longitude = 0<br/> 
     * Height = 0<br/>
     */
    public Coordinates() {
        this.Latitude = 0;
        this.Longitude = 0;
        this.Height = 0;
        this.Bitacora = null;
    }

    /**
     * Constructor de inicializacion del punto coordenado<br/><br/>
     *
     * @param Latitude Latitude del punto -90(S) a 90(N)
     * @param Longitude Longitud del punto -180(W) a 180(E)
     * @param Height Altura del punto en (mts) respecto al nivel del mar
     */
    public Coordinates(double Longitude,double Latitude, double Height) {
        this.Latitude = Latitude;
        this.Longitude = Longitude;
        this.Height = Height;
        this.Bitacora = null;
    }
    
    /**
     * Constructor de inicializacion del punto coordenado con manejador de bitacora<br/><br/>
     *
     * @param Latitude Latitude del punto -90(S) a 90(N)
     * @param Longitude Longitud del punto -180(W) a 180(E)
     * @param Height Altura del punto en (mts) respecto al nivel del mar
     * @param Log Apuntador al manejador de bitacora
     */
    public Coordinates(double Longitude,double Latitude, double Height, LoggerManager Log) {
        this.Latitude = Latitude;
        this.Longitude = Longitude;
        this.Height = Height;
        this.Bitacora = Log;
    }

    /**
     * Constructor de inicializacion del punto coordenado, la altura se
     * establece en 0 mts sobre el nivel del mar.<br/><br/>
     *
     * @param Latitude Latitude del punto -90(S) a 90(N)
     * @param Longitude Longitud del punto -180(W) a 180(E)
     */
    public Coordinates(double Longitude,double Latitude) {
        this.Latitude = Latitude;
        this.Longitude = Longitude;
        this.Height = 0;
        this.Bitacora = null;
    }

    /**
     * Constructor de copiado<br/><br/>
     *
     * @param Other
     */
    public Coordinates(Coordinates Other) {
        this.Latitude = Other.Latitude;
        this.Longitude = Other.Longitude;
        this.Height = Other.Height;
        this.Bitacora = Other.Bitacora;
    }

    /**
     * Establece la Bitacora que se va a utilizar para reportar eventos en el sistema.
     * 
     * @param Bitacora Apuntador a la bitacora
     */
    public void setBitacora(LoggerManager Log) {
        this.Bitacora = Log;
    }
    
    /**
     * Retorna la altura del punto (mts) respecto al nivel del mar<br/><br/>
     *
     * @return Altura (mts) respecto al nivel del mar.
     */
    public double getHeight() {
        return Height;
    }

    /**
     * Establece la altura del punto (mts) respecto al nivel del mar<br/><br/>
     *
     * @param Height Altura (mts) respecto al nivel del mar.
     */
    public void setHeight(double Height) {
        this.Height = Height;
    }

    /**
     * Retorna la Latitude del punto -90(S) a 90(N)<br/><br/>
     *
     * @return Latitude
     */
    public double getLatitude() {
        return Latitude;
    }

    /**
     * Establece la Latitude del punto -90(S) a 90(N)<br/><br/>
     *
     * @param Latitude
     */
    public void setLatitude(double Latitude) {
        this.Latitude = Latitude;
    }

    /**
     * Retorna la longitud del punto -180(W) a 180(E)<br/><br/>
     *
     * @return Longitud del punto
     */
    public double getLongitude() {
        return Longitude;
    }

    /**
     * Establece la longitud del punto -180(W) a 180(E)<br/><br/>
     *
     * @param Longitude Longitude del punto
     */
    public void setLongitude(double Longitude) {
        this.Longitude = Longitude;
    }
    
    /**
     * Calcula el radio medio (mts) de la tierra  entre dos latitudes, si las latitudes
     * estan opuestas respecto al ecuador se usa el radio ecuatorial de la tierra.
     * 
     * r= (Re^2*Rp^2)/(Re^2*SIN^2((LatA+LatB)*Pi/(2*180))+Rp^2*COS^2((LatA+LatB)*Pi/(2*180))
     * 
     * @param LatA
     * @param LatB
     * @return Radio medio en mts 
     */
    protected double RadioMedio(double LatA,double LatB){
        double R;
        
        if(((LatA>0)&&(LatB<0))||((LatA<0)&&(LatB>0))){
           R = Coordinates.RadioEquatorial;   
        }else {
        R=Math.sqrt((Math.pow(Coordinates.RadioEquatorial,2)*Math.pow(Coordinates.RadioPolar,2))/
          (Math.pow(Coordinates.RadioEquatorial,2)*Math.pow(Math.sin((LatA+LatB)*Math.PI/360),2)+Math.pow(Coordinates.RadioPolar,2)*Math.pow(Math.cos((LatA+LatB)*Math.PI/360),2)));
        }
        return R;
    }
    
    /**
     * Calcula la distancia en metros entre dos puntos coordenados sobre la
     * superficie de la tierra
     * 
     * @param B Punto coordenado sobre la tierra para calcular su distancia
     * @return  Distancia (mts) entre ambos puntos coordenados
     */
    public double Distance(Coordinates B){
        double Distancia;
        
       
       //  Dist = Rmedio * ACOS(COS(LatA*Pi/180) * COS(LatB*Pi/180) * COS((LngB - LngA)*Pi/180) + SIN(LatA*Pi/180) * SIN(LatB*Pi/180)) 
       Distancia=RadioMedio(this.Latitude,B.Latitude)*Math.acos(         
       Math.cos(this.Latitude*Math.PI/180)*Math.cos(B.Latitude*Math.PI/180)*Math.cos((B.Longitude-this.Longitude)*Math.PI/180)+
       Math.sin(this.Latitude*Math.PI/180)*Math.sin(B.Latitude*Math.PI/180));
       return Distancia;
    }
    
    /**
     * Calcula la distancia 3D en metros entre dos puntos coordenados sobre la
     * superficie de la tierra
     * 
     * @param B Punto coordenado con altura sobre la tierra para calcular su distancia
     * @return  Distancia (mts) entre ambos puntos coordenados en el espacio
     */
    public double Distance3D(Coordinates B){
        double Distancia,DeltaAltura;
       
       Distancia=this.Distance(B);
       DeltaAltura = Math.abs(this.Height-B.Height);
       Distancia = Math.sqrt(Math.pow(Distancia,2)+Math.pow(DeltaAltura,2));
       return Distancia;
    }

    /**
     * Calcula el Azimut desde el punto coordenado actual hacia el punto coordenado B
     * 
     * @param B  Punto coordenado hacia donde calcular el azumut
     * @return   Azimut en grados.
     */
    public double Azimut(Coordinates B){
        double azimutc,dt,dlon;
        double ALatitude,ALongitude,BLatitude,BLongitude;
        
        ALatitude=this.Latitude;
        ALongitude=this.Longitude;
        BLatitude=B.Latitude;
        BLongitude=B.Longitude;
        dt = Math.log( Math.tan( (Math.PI/4)*((BLatitude/90) + 1 )) / Math.tan( (Math.PI/4)*((ALatitude/90) + 1) ));
        dlon = Math.PI*( BLongitude - ALongitude )/180;
        azimutc = Math.atan2(dlon,dt);
        if(azimutc<0){
            azimutc+=2*Math.PI;
        }
        return azimutc*180/Math.PI;
    }

    /**
     *  Retorna el punto de destino dada una distancia y un azimut
     * 
     * @param Distance
     * @param Azimut
     * @return
     */
    public Coordinates Destination(double Distance, double Azimut) {
        double lat1, lat2;
        double lon1, lon2;
        NewtonRaphson NR;
        double x[];
        NAFunction Fx[];

        x = new double[2];
        Fx = new NAFunction[2];
        lat1 = (Math.PI * this.Latitude) / 180;
        lon1 = (Math.PI * this.Longitude) / 180;
        Azimut = (Math.PI * Azimut) / 180;
        Fx[0] = new DestinationLatEquation(lon1, lat1, Distance, Azimut);
        Fx[1] = new DestinationLonEquation(lon1, lat1, Distance, Azimut);
        NR = new NewtonRaphson(Fx);
        x[0] = lon1;
        x[1] = lat1;
        NR.setError(1e-20);
        try {
            NR.setInicialEstimation(x);
            x = NR.Resolve();
        } catch (UtilsException ex) {
            if (this.Bitacora != null) {
                this.Bitacora.LogMessage(LoggerManager.TYPE_ERROR, LoggerManager.LEVEL_ERROR, Coordinates.class.getName(), ex.getMessage());
            } else {
                Logger.getLogger(Coordinates.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        lon2 = 180 * x[0] / Math.PI;
        lat2 = 180 * x[1] / Math.PI;
        return new Coordinates(lon2, lat2);
    }
    
    @Override
    public long Metrica() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Utiliza como metrica el ordenar las coordenadas respecto a su distancia
     * de la intercepcion del meridiano de Greenwich y el ecuador o coordenada (0,0).
     * 
     * @return Distancia respecto con el punto coordenado (0,0)
     */
    @Override
    public double MetricaDouble() {
        double Distancia;
        
        Distancia = this.Distance(new Coordinates());
        return Distancia;
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int Order(Sortable Other) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
