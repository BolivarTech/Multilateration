package com.bolivartech.position;

/**
 * Copyright 2010,2011,2012 Valhala Networks C.A.<br/>
 *
 *  <p>Homepage: <a href="http://www.cuaimacrypt.com">http://www.cuaimacrypt.com</a>.</p>
 *  <p>Valhala Networks Homepage: <a href="http://www.valhalanetworks.com">http://www.valhalanetworks.com</a>.</p>
 *
 *   This Class is the Valhala Networks's Target element Class.<br/><br/>
 *
 *   Esta clase implementa la definicion de un elemento Target (objetivop) para el posicionamiento en JAVA.<br/><br/><br/><br/>
 *
 *   <br/><br/>
 *
 * @author Julian Bolivar
 * @since 2007
 * @date July 22, 2012.
 * @version 1.0.0
 */
public class Target extends Coordinates {
    double Error;  // Error de calculo de las coordenadas

    public Target(Target Other) {
        super(Other);
        this.Error = Other.Error;
    }

    public Target(double Longitude, double Latitude,double Height) {
        super(Longitude, Latitude,Height);
        this.Error = 0;
    }

    public Target(double Longitude, double Latitude, double Height,double Error ) {
        super(Longitude, Latitude, Height);
        this.Error = Error;
    }

    public double getError() {
        return Error;
    }

    public void setError(double Error) {
        this.Error = Error;
    }

    /**
     * Utiliza como metrica el ordenar las coordenadas respecto al Error del calculo
     * de posicion del targer
     * 
     * @return Error de calculo del target
     */
    @Override
    public double MetricaDouble() {
        
        return Error;
        //throw new UnsupportedOperationException("Not supported yet.");
    }   
    
    
}
