package com.bolivartech.position;

import com.valhala.utils.exception.UtilsException;
import com.valhala.utils.numericalalgorith.NAFunction;

/**
 *
 * @author jbolivarg
 */
public class DestinationLonEquation extends NAFunction {
    private double lat1; 
    private double lon1;
    private double Distance,Azimut;

    public DestinationLonEquation(double lon1, double lat1, double Distance, double Azimut) {
        this.lat1 = lat1;
        this.lon1 = lon1;
        this.Distance = Distance;
        this.Azimut = Azimut;
    }
    
    @Override
    public double Eval(double[] x) throws UtilsException {
        double lat2; 
        double lon2;
        double centralAngle;
        
        lon2 = x[0];
        lat2 = x[1];
        centralAngle=Distance/this.RadioMedio(180*lat1/Math.PI, 180*lat2/Math.PI);
        return lon1 + Math.atan2(Math.sin( Azimut) * Math.sin(centralAngle) * Math.cos(lat1), Math.cos(centralAngle) - Math.sin(lat1) * Math.sin(Math.asin(Math.sin(lat1) * Math.cos(centralAngle) + Math.cos(lat1) * Math.sin(centralAngle) * Math.cos(Azimut))))-lon2;

    }

    @Override
    public int Dimension() {
        return 2;
        //throw new UnsupportedOperationException("Not supported yet.");
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

    
}
