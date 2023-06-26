package com.bolivartech.position;

import com.valhala.utils.exception.UtilsException;
import com.valhala.utils.numericalalgorith.NAFunction;

/**
 * Copyright 2010,2011,2012 Valhala Networks C.A.<br/>
 *
 *  <p>Homepage: <a href="http://www.cuaimacrypt.com">http://www.cuaimacrypt.com</a>.</p>
 *  <p>Valhala Networks Homepage: <a href="http://www.valhalanetworks.com">http://www.valhalanetworks.com</a>.</p>
 *
 *   This Class is the Valhala Networks's 2D Time Difference equation Class.<br/><br/>
 *
 *   Esta clase implementa la definicion de la ecuacion para la diferencia de tiempo de llegada en 2 dimensiones.<br/><br/><br/><br/>
 *
 *   <br/><br/>
 *
 * @author Julian Bolivar
 * @since 2007
 * @date July 22, 2012.
 * @version 1.0.0
 */
public strictfp class TimeDifference2D extends NAFunction  {
    double SignalSpeed;
    double TimeDiff;
    double TimeDiff1;
    double Xp,Yp;
    double X1,Y1;
    
    /** Velocidad de la luz (m/s) */
    public final static double LightSpeed = 299792458;  // Velocidad de la Luz en m/s

    /**
     * Constructor con inicializacion de diferencia de coordenadas.<br/><br/>
     * 
     * La velocidad se establece la de la luz.<br/>
     * La diferencia de tiempo se establece en 0.<br/><br/>
     * 
     * @param Xp  Difecencia del TrianguladorP coordenadas respecto al pibote en la Longitud
     * @param Yp  Difecencia del TrianguladorP coordenadas respecto al pibote en la Latitud
     * @param Zp  Difecencia del TrianguladorP alturas respecto al pibote en metros
     * @param X1  Difecencia del Triangulador1 coordenadas respecto al pibote en la Longitud
     * @param Y1  Difecencia del Triangulador1 coordenadas respecto al pibote en la Latitud
     * @param Z1  Difecencia del Triangulador1 alturas respecto al pibote en metros
     */
    public TimeDifference2D(double Xp, double Yp, double X1, double Y1) {
        this.Xp = Xp;
        this.Yp = Yp;
        this.X1 = X1;
        this.Y1 = Y1;
        this.TimeDiff = 0;
        this.TimeDiff1 = 0;
        this.SignalSpeed = TimeDifference3D.LightSpeed;  // Velocidad de la Luz en m/s
    }

    /**
     * Constructor con inicializacion de diferencia de coordenadas, velocidad e la señal y diferencias de tiempo.<br/><br/>
     * 
     * @param SignalSpeed  Velocidad de la señal en m/s
     * @param TimeDiff     Diferencia de tiempo del TrianguladorP despecto al pibote en segundos
     * @param Xp  Difecencia del TrianguladorP coordenadas respecto al pibote en la Longitud
     * @param Yp  Difecencia del TrianguladorP coordenadas respecto al pibote en la Latitud
     * @param Zp  Difecencia del TrianguladorP alturas respecto al pibote en metros
     * @param TimeDiff1     Diferencia de tiempo del Triangulador1 despecto al pibote en segundos
     * @param X1  Difecencia del Triangulador1 coordenadas respecto al pibote en la Longitud
     * @param Y1  Difecencia del Triangulador1 coordenadas respecto al pibote en la Latitud
     * @param Z1  Difecencia del Triangulador1 alturas respecto al pibote en metros
     */
    public TimeDifference2D(double SignalSpeed, double TimeDiff, double Xp, double Yp, double TimeDiff1, double X1, double Y1) {
        this.SignalSpeed = SignalSpeed;
        this.TimeDiff = TimeDiff;
        this.TimeDiff1 = TimeDiff1;
        this.Xp = Xp;
        this.Yp = Yp;
        this.X1 = X1;
        this.Y1 = Y1;
    }

    /**
     * Constructor con inicializacion de diferencia de coordenadas, velocidad e la señal y diferencias de tiempo.<br/><br/>
     *
     * La velocidad se establece la de la luz.<br/>
     * 
     * @param TimeDiff     Diferencia de tiempo de recepcion despecto al pibote en segundos
     * @param Xp  Difecencia del TrianguladorP coordenadas respecto al pibote en la Longitud
     * @param Yp  Difecencia del TrianguladorP coordenadas respecto al pibote en la Latitud
     * @param Zp  Difecencia del TrianguladorP alturas respecto al pibote en metros
     * @param X1  Difecencia del Triangulador1 coordenadas respecto al pibote en la Longitud
     * @param Y1  Difecencia del Triangulador1 coordenadas respecto al pibote en la Latitud
     * @param Z1  Difecencia del Triangulador1 alturas respecto al pibote en metros
     */
    public TimeDifference2D(double TimeDiff, double Xp, double Yp, double X1, double Y1) {
        this.SignalSpeed = TimeDifference3D.LightSpeed; 
        this.Xp = Xp;
        this.Yp = Yp;
        this.X1 = X1;
        this.Y1 = Y1;
        this.TimeDiff = TimeDiff;
    }

    /**
     * Retorna la velocidad de propagacion de la señal en el sistema.<br/><br/>
     * 
     * @return velocidad de propagacion en m/s
     */
    public double getSignalSpeed() {
        return SignalSpeed;
    }

    /**
     * Establece la velocidad de propagacion de la señal en el sistema en m/s.<br/><br/>
     * 
     * @param SignalSpeed Velocidad de propagacion en m/s
     */
    public void setSignalSpeed(double SignalSpeed) {
        this.SignalSpeed = SignalSpeed;
    }

    /**
     * Retorna la diferencia de tiempo de recepcion despecto al pibote en segundos.<br/><br/>
     * 
     * @return Diferencia de tiempo de recepcion despecto al pibote (segundos)
     */
    public double getTimeDiff() {
        return TimeDiff;
    }

    /**
     * Establece la diferencia de tiempo de recepcion despecto al pibote en segundos.<br/><br/>
     * 
     * @param TimeDiff  Diferencia de tiempo de recepcion despecto al pibote (segundos)
     */
    public void setTimeDiff(double TimeDiff) {
        this.TimeDiff = TimeDiff;
    }

    /**
     * Retorna la difecencia de coordenadas respecto al pibote en la Longitud.<br/><br/>
     * 
     * @return Difecencia de coordenadas respecto al pibote en la Longitud en metros
     */
    public double getXp() {
        return Xp;
    }

    /**
     * Establece la difecencia de coordenadas respecto al pibote en la Longitud.<br/><br/>
     * 
     * @param Xp Difecencia de coordenadas respecto al pibote en la Longitud en metros
     */
    public void setXp(double Xp) {
        this.Xp = Xp;
    }

    /**
     * Retorna la difecencia de coordenadas respecto al pibote en la Latitud.<br/><br/>
     * 
     * @return Difecencia de coordenadas respecto al pibote en la latitud en metros
     */
    public double getYp() {
        return Yp;
    }

    /**
     * Establece la difecencia de coordenadas respecto al pibote en la Latitud.<br/><br/>
     * 
     * @param Yp Difecencia de coordenadas respecto al pibote en la Latitud en metros
     */
    public void setYp(double Yp) {
        this.Yp = Yp;
    }


    /**
     * Retorna la difecencia de coordenadas respecto al pibote en la Longitud.<br/><br/>
     * 
     * @return Difecencia de coordenadas respecto al pibote en la Longitud en metros
     */
    public double getX1() {
        return X1;
    }

    /**
     * Establece la difecencia de coordenadas respecto al pibote en la Longitud.<br/><br/>
     *
     * @param X1 Difecencia de coordenadas respecto al pibote en la Longitud en metros
     */
    public void setX1(double X1) {
        this.Xp = X1;
    }

    /**
     * Retorna la difecencia de coordenadas respecto al pibote en la Latitud.<br/><br/>
     * 
     * @return Difecencia de coordenadas respecto al pibote en la latitud en metros
     */
    public double getY1() {
        return Y1;
    }

    /**
     * Establece la difecencia de coordenadas respecto al pibote en la Latitud.<br/><br/>
     * 
     * @param Y1 Difecencia de coordenadas respecto al pibote en la Latitud en metros
     */
    public void setY1(double Y1) {
        this.Y1 = Y1;
    }

    
    @Override
    public double Eval(double[] x) throws UtilsException {
        double Am,Bm,Dm;
        
        Am=((2*this.Xp)/(this.SignalSpeed*this.TimeDiff))-((2*this.X1)/(this.SignalSpeed*this.TimeDiff1));
        Bm=((2*this.Yp)/(this.SignalSpeed*this.TimeDiff))-((2*this.Y1)/(this.SignalSpeed*this.TimeDiff1));
        Dm=(this.SignalSpeed*this.TimeDiff)-(this.SignalSpeed*this.TimeDiff1)-((Math.pow(this.Xp,2)+Math.pow(this.Yp,2))/(this.SignalSpeed*this.TimeDiff))+((Math.pow(this.X1,2)+Math.pow(this.Y1,2))/(this.SignalSpeed*this.TimeDiff1));
        return x[0]*Am+x[1]*Bm+Dm;
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int Dimension() {
        return 2;
        //throw new UnsupportedOperationException("Not supported yet.");
    }
}
