package com.bolivartech.position;

import com.valhala.utils.array.ArrayUtils;
import com.valhala.utils.exception.UtilsException;
import com.valhala.utils.numericalalgorith.Matrix;
import com.valhala.utils.numericalalgorith.NewtonRaphson;
import com.valhala.utils.random.MersenneTwisterPlus;
import com.valhala.utils.sort.Searcher;
import com.valhala.utils.sort.Sorter;

/**
 * Copyright 2010,2011,2012 Valhala Networks C.A.<br/>
 *
 * <p>Homepage: <a
 * href="http://www.cuaimacrypt.com">http://www.cuaimacrypt.com</a>.</p>
 * <p>Valhala Networks Homepage: <a
 * href="http://www.valhalanetworks.com">http://www.valhalanetworks.com</a>.</p>
 *
 * This Class is the Valhala Networks's TDOA (Time Difference of Arrival)
 * Class.<br/><br/>
 *
 * Esta clase implementa el algoritmo TDOA (Time Difference of Arrival) para el
 * posicionamiento en JAVA.<br/><br/><br/><br/>
 *
 * <br/><br/>
 *
 * @author Julian Bolivar
 * @since 2007 @date July 22, 2012.
 * @version 1.0.0
 */
public strictfp class TDOA {

    double SignalSpeed;
    Triangulador Trianguladores[];
    boolean IDSorded;
    boolean TimeArrivalSorded;
    /**
     * Velocidad de la luz (m/s)
     */
    public final static double LightSpeed = 299792458;  // Velocidad de la Luz en m/s
    /**
     * No hay suficientes trianguladores para localizar
     */
    public final static int NoEnoughTrianguladores = 1;

    /**
     * Ordena los trianguladores por ID
     */
    private void SortByID() {
        Sorter.RelativeSort(this.Trianguladores, Sorter.ASCENDING);
        IDSorded = true;
        TimeArrivalSorded = false;
    }

    /**
     * Ordena los trianguladores respecto al tiempo de llegada
     */
    private void SortByTimeArrival() {
        Sorter.AbsoluteSort(this.Trianguladores, Sorter.ASCENDING);
        IDSorded = false;
        TimeArrivalSorded = true;
    }

    /**
     * Constructor de TDOA con inicializacion de los elementos de triangulacion
     *
     * @param Trianguladores
     */
    public TDOA(Triangulador[] Trianguladores) {
        this.Trianguladores = Trianguladores;
        IDSorded = false;
        TimeArrivalSorded = false;
        SignalSpeed = TDOA.LightSpeed * 1e-9;  // Velociadad de la luz en (m/ns)
    }

    /**
     * Retorna los trianguladores contenidos en el TDOA ordenados por ID
     *
     * @return Trianguladores
     */
    public Triangulador[] getTrianguladores() {
        // Verifica si la lista de Trianguladores esta ordenado por ID
        if (!IDSorded) {
            SortByID();
        }
        return Trianguladores;
    }

    /**
     * Establece los trianguladores en el TDOA
     *
     * @param Trianguladores
     */
    public void setTrianguladores(Triangulador[] Trianguladores) {
        this.Trianguladores = Trianguladores;
        IDSorded = false;
        TimeArrivalSorded = false;
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
     * Establece la velocidad de propagacion de la señal en el sistema en
     * m/s.<br/><br/>
     *
     * @param SignalSpeed Velocidad de propagacion en m/s
     */
    public void setSignalSpeed(double SignalSpeed) {
        this.SignalSpeed = SignalSpeed;
    }

    /**
     * Establece el Time Arrival al Triangulador identificado con ID
     *
     * @param ID Del Triangulador a establecerle el TimeArrival
     * @param TimeArrival Valor del TimeArrival a almacenar
     */
    public void setTimeArrival(int ID, long TimeArrival) {
        int Pos;

        if (this.Trianguladores != null) {
            // Verifica si la lista de Trianguladores esta ordenado por ID
            if (!IDSorded) {
                SortByID();
            }
            Pos = Searcher.RelativeSearch(this.Trianguladores, new Triangulador(ID, 0, 0, 0));
            if (Pos >= 0) {
                this.Trianguladores[Pos].setTimeArrival(TimeArrival);
            }
        }
    }

    /**
     * Retorna el Time Arrival del Triangulador identificado con ID
     *
     * @param ID Del Triangulador a buscar
     * @return TimeArrival almacenado en el triangulador,-1 si no encontro el ID
     */
    public long getTimeArrival(int ID) {
        int Pos;
        long Salida;

        Salida = -1;
        if (this.Trianguladores != null) {
            // Verifica si la lista de Trianguladores esta ordenado por ID
            if (!IDSorded) {
                SortByID();
            }
            Pos = Searcher.RelativeSearch(this.Trianguladores, new Triangulador(ID, 0, 0, 0));
            if (Pos >= 0) {
                Salida = this.Trianguladores[Pos].getTimeArrival();
            }
        }
        return Salida;
    }

    /**
     * Verifica si existe el triangulador identificado con ID dentro del TDOA
     *
     * @param ID del identificador a buscar
     * @return true si existe y false si no
     */
    public boolean ExistTriangulador(int ID) {
        int Pos;
        boolean Salida;

        Salida = false;
        if (this.Trianguladores != null) {
            // Verifica si la lista de Trianguladores esta ordenado por ID
            if (!IDSorded) {
                SortByID();
            }
            Pos = Searcher.RelativeSearch(this.Trianguladores, new Triangulador(ID, 0, 0, 0));
            if (Pos >= 0) {
                Salida = true;
            }
        }
        return Salida;
    }

    /*
     * Si se disponen solo de 4 trianguladores se realiza la ubicacion en 2D
     */
    private Coordinates Locate2D() throws UtilsException {
        NewtonRaphson NR;
        TimeDifference2D TDEq[];
        double TCoordenadas[];
        double TLatitud, TLongitud, Varianza, MinError;
        int i, j;
        boolean Nula;
        Triangulador TriangTemp;
        MersenneTwisterPlus Aleatorio;

        Aleatorio = new MersenneTwisterPlus();
        Varianza = 0.3;
        TCoordenadas = new double[2];
        TLatitud = 0;
        TLongitud = 0;
        // Verifica si la lista de Trianguladores esta ordenado por Time Arrival
        if (!TimeArrivalSorded) {
            SortByTimeArrival();
        }
        // Establece las ecuaciones para ubicar basado el tiempo de diferencia 
        TDEq = new TimeDifference2D[2];
        TDEq[0] = new TimeDifference2D(this.SignalSpeed, Trianguladores[0].TimeDiff(Trianguladores[2]), Trianguladores[0].LongitudeLinealDiff(Trianguladores[2]), Trianguladores[0].LatitudeLinealDiff(Trianguladores[2]),Trianguladores[0].TimeDiff(Trianguladores[1]),Trianguladores[0].LongitudeLinealDiff(Trianguladores[1]),Trianguladores[0].LatitudeLinealDiff(Trianguladores[1]));
        TDEq[1] = new TimeDifference2D(this.SignalSpeed, Trianguladores[0].TimeDiff(Trianguladores[3]), Trianguladores[0].LongitudeLinealDiff(Trianguladores[3]), Trianguladores[0].LatitudeLinealDiff(Trianguladores[3]),Trianguladores[0].TimeDiff(Trianguladores[1]),Trianguladores[0].LongitudeLinealDiff(Trianguladores[1]),Trianguladores[0].LatitudeLinealDiff(Trianguladores[1]));
        i = 0;
        MinError = 1000;
        Nula = false;
        do {
            NR = new NewtonRaphson(TDEq);
            NR.setError(1e-6);
            NR.setErrorMagnifier(true);
            NR.setMaxNumIterations(10000);
            if (Nula) {
                TCoordenadas[0] = TLongitud * (1 + (Varianza * Aleatorio.nextDouble()));
                TCoordenadas[1] = TLatitud * (1 + (Varianza * Aleatorio.nextDouble()));
                Nula = false;
            } else {
                //Busca el centroide de todos los trianguladores como punton inicial
                TCoordenadas[0] = 0;
                TCoordenadas[1] = 0;
                for (j = 0; j < Trianguladores.length; j++) {
                    TCoordenadas[0] += Trianguladores[j].Longitude;
                    TCoordenadas[1] += Trianguladores[j].Latitude;
                }
                TCoordenadas[0] /= Trianguladores.length;
                TCoordenadas[1] /= Trianguladores.length;
                TCoordenadas[0] *= (1 + (Varianza * Aleatorio.nextDouble()));
                TCoordenadas[1] *= (1 + (Varianza * Aleatorio.nextDouble()));
                TriangTemp = new Triangulador(-1, TCoordenadas[0], TCoordenadas[1],0);
                TCoordenadas[0] = Trianguladores[0].LongitudeLinealDiff(TriangTemp);
                TCoordenadas[1] = Trianguladores[0].LatitudeLinealDiff(TriangTemp);
                Nula = false;
            }
            try {
                NR.setInicialEstimation(TCoordenadas);
                TCoordenadas = NR.Resolve();
                TLongitud = TCoordenadas[0];
                TLatitud = TCoordenadas[1];
                MinError = NR.getMinError();
            } catch (UtilsException ex) {
                if (ex.getMessage().equalsIgnoreCase("ERROR: Matriz Singular")) {
                    if (ex.getErrorCode() == Matrix.SINGULAR_MATRIX) {
                        // No se toma en cuenta y sigue con el siguiente
                        //System.out.println("----> " + ex.getMessage());
                        Nula = true;
                        Varianza += 0.01;
                    }
                }
            }
            i++;
        } while (Nula && (i < 50));
        // Convierte la Latitud y la Longitud de lineal a angular 
        TLongitud = Trianguladores[0].Lineal2AngLongitudeDiff(TLongitud);
        TLatitud = Trianguladores[0].Lineal2AngLatitudeDiff(TLatitud);
        if (i < 50) {
            // Convierte las diferencias referidas al Triangulador pibote en valores absolutos
            TLongitud += Trianguladores[0].getLongitude();
            TLatitud += Trianguladores[0].getLatitude();
        } else {
            TLongitud = -1000;
            TLatitud = -1000;
        }
        return new Coordinates(TLongitud, TLatitud, 0);
    }

    /*
     * Realiza el analisis de los resultados obteniendo el resultado que mejor
     * aproxime la respuesta
     */
    private Target LeastSquares(Target[] Objetivos, int NumObjetivos) {
        Target Salida;
        double Media, Saltos, D1, D2;
        double Latitud, Longitud, Altura;
        int i, j;
        int Validos;
        int Intervalos;
        int[] Distribucion;

        Intervalos = 1000000;
        Media = 0;
        Latitud = 0;
        Longitud = 0;
        Altura = 0;
        Salida = null;
        if ((Objetivos != null) && (NumObjetivos > 1)) {
            // Elimina los datos nulos
            Objetivos = (Target[]) ArrayUtils.arrayTrim(Objetivos);
            // Ordena los Objetivos por el error de ubucacion
            Sorter.AbsoluteSortDouble(Objetivos, Sorter.ASCENDING);
            // Calcula la media cuadratica del error
/*
             * for(i=0;i<NumObjetivos;i++){ Media +=
             * Math.pow(Objetivos[i].getError(),2); } Media /= NumObjetivos;
             * Media = Math.sqrt(Media);
             */
            // Busca la moda
            Saltos = (Objetivos[NumObjetivos - 1].getError() - Objetivos[0].getError()) / (Intervalos - 1);
            Distribucion = new int[Intervalos];
            for (i = 0; i < NumObjetivos; i++) {
                for (j = 0; j < Intervalos; j++) {
                    if ((Objetivos[i].getError() >= (j * Saltos)) && (Objetivos[i].getError() < ((j + 1) * Saltos))) {
                        Distribucion[j]++;
                        j = Intervalos;
                    }
                }
            }
            // Busca el maximo
            j = 0;
            for (i = 0; i < Intervalos; i++) {
                if (Distribucion[i] > Distribucion[j]) {
                    j = i;
                }
            }
            if (j == 0) {
                D1 = Distribucion[0];
            } else {
                D1 = Distribucion[j] - Distribucion[j - 1];
            }
            if (j == Intervalos - 1) {
                D2 = Distribucion[Intervalos - 1];
            } else {
                D2 = Distribucion[j] - Distribucion[j + 1];
            }
            Media = Saltos * j + ((D1 * Saltos) / (D1 + D2));
            Latitud = 0;
            Longitud = 0;
            Altura = 0;
            Validos = 0;
            for (i = 0; i < NumObjetivos; i++) {
                if ((Objetivos[i].getError()>=(j*Saltos))&&(Objetivos[i].getError() < ((j+1)*Saltos))) {
                //if (Objetivos[i].getError()<Media) {    
                    Longitud += Objetivos[i].getLongitude();
                    Latitud += Objetivos[i].getLatitude();
                    Altura += Objetivos[i].getHeight();
                    Validos++;
                }
            }
            Longitud /= Validos;
            Latitud /= Validos;
            Altura /= Validos;
/*            Longitud = Math.sqrt(Longitud);
            Latitud = Math.sqrt(Latitud);
            Altura = Math.sqrt(Altura);   */
        }else if(NumObjetivos == 1){
            Longitud = Objetivos[0].getLongitude();
            Latitud = Objetivos[0].getLatitude();
            Altura = Objetivos[0].getHeight();
        }
        Salida = new Target(Longitud, Latitud, Altura, Media);
        return Salida;
    }
    
    /*
     * Si se disponen de 5 o mas trianguladores se realiza la ubicacion 3D
     */
    private Coordinates Locate3D() throws UtilsException {
        NewtonRaphson NR;
        TimeDifference3D TDEq[];
        double TCoordenadas[];
        double TLatitud, TLongitud, TAltura, MinError, MaxError, Varianza;
        int i, j, ciclos;
        Triangulador TriangTemp;
        boolean Nula;
        MersenneTwisterPlus Aleatorio;
        Target Objetivos[];
        int NumObjetivos;

        Aleatorio = new MersenneTwisterPlus();
        MaxError = 1;
        Varianza = 0.3;
        TLatitud = (Aleatorio.nextShort15() % 180) - 90;
        TLongitud = (Aleatorio.nextShort15() % 360) - 180;
        TAltura = 0;
        j = Trianguladores.length - 4;
        if (j > 1) {
            Objetivos = new Target[j];
            for (i = 0; i < Objetivos.length; i++) {
                Objetivos[i] = null;
            }
        } else {
            Objetivos = new Target[1];
            Objetivos[0] = null;
        }
        NumObjetivos = 0;
        // Verifica si la lista de Trianguladores esta ordenado por Time Arrival
        if (!TimeArrivalSorded) {
            SortByTimeArrival();
        }
        // Establece las ecuaciones para ubicar basado el tiempo de diferencia 
        TDEq = new TimeDifference3D[3];
        // Inicial el recorrido de todos los trianguladores disponibles
        // fijando el triangulador 0 (el mas cercano) como referencia
        Nula = false;
        MinError = 10 * MaxError;
        ciclos = 0;
        for (i = 2; i < Trianguladores.length - 2; i++) {
            // Selecciona un grupo de 3 trainguladores para establecer las coordenadas
            for (j = i; j < i + 3; j++) {
                TDEq[j - i] = new TimeDifference3D(this.SignalSpeed, Trianguladores[0].TimeDiff(Trianguladores[j]), Trianguladores[0].LongitudeLinealDiff(Trianguladores[j]), Trianguladores[0].LatitudeLinealDiff(Trianguladores[j]), Trianguladores[0].HeightDiff(Trianguladores[j]), Trianguladores[0].TimeDiff(Trianguladores[1]), Trianguladores[0].LongitudeLinealDiff(Trianguladores[1]), Trianguladores[0].LatitudeLinealDiff(Trianguladores[1]), Trianguladores[0].HeightDiff(Trianguladores[1]));
            }
            NR = new NewtonRaphson(TDEq);
            NR.setError(1e-9);
            NR.setErrorMagnifier(true);
            TCoordenadas = new double[3];
            if (Nula) {
                TCoordenadas[0] = TLongitud * (1 + (Varianza * Aleatorio.nextDouble()));
                TCoordenadas[1] = TLatitud * (1 + (Varianza * Aleatorio.nextDouble()));
                TCoordenadas[2] = TAltura * (1 + (Varianza * Aleatorio.nextDouble()));
                Nula = false;
            } else {
                //Busca el centroide de todos los trianguladores como punton inicial
                TCoordenadas[0] = 0;
                TCoordenadas[1] = 0;
                TCoordenadas[2] = 0;
                for (j = 0; j < Trianguladores.length; j++) {
                    TCoordenadas[0] += Trianguladores[j].Longitude;
                    TCoordenadas[1] += Trianguladores[j].Latitude;
                    TCoordenadas[2] += Trianguladores[j].Height;
                }
                TCoordenadas[0] /= Trianguladores.length;
                TCoordenadas[1] /= Trianguladores.length;
                TCoordenadas[2] /= Trianguladores.length;
                TCoordenadas[0] *= (1 + (Varianza * Aleatorio.nextDouble()));
                TCoordenadas[1] *= (1 + (Varianza * Aleatorio.nextDouble()));
                TCoordenadas[2] *= (1 + (Varianza * Aleatorio.nextDouble()));
                TriangTemp = new Triangulador(-1, TCoordenadas[0], TCoordenadas[1], TCoordenadas[2]);
                TCoordenadas[0] = Trianguladores[0].LongitudeLinealDiff(TriangTemp);
                TCoordenadas[1] = Trianguladores[0].LatitudeLinealDiff(TriangTemp);
                TCoordenadas[2] = Trianguladores[0].HeightDiff(TriangTemp);
                Nula = false;
            }
            NR.setInicialEstimation(TCoordenadas);
            NR.setMaxNumIterations(10000);
            try {
                TCoordenadas = NR.Resolve();
                if(NumObjetivos<Objetivos.length){
                    Objetivos[NumObjetivos] = new Target(TCoordenadas[0], TCoordenadas[1], TCoordenadas[2], NR.getMinError());
                    NumObjetivos++;
                }else{
                    // Ordena los Objetivos por el error de ubicacion
                    Sorter.AbsoluteSortDouble(Objetivos,Sorter.DESCENDING);
                    if(Objetivos[0].getError()>NR.getMinError()){
                        // Elimina el peor error si el nuevo error es mejor
                       Objetivos[0] = new Target(TCoordenadas[0], TCoordenadas[1], TCoordenadas[2], NR.getMinError());
                    }
                }
                ciclos = 0;
                /*
                 * if (NR.getMinError() < MinError) { TLongitud =
                 * TCoordenadas[0]; TLatitud = TCoordenadas[1]; TAltura =
                 * TCoordenadas[2]; MinError = NR.getMinError();
                 * //System.out.println("Error Minimo: " +
                 * String.valueOf(MinError)); ciclos = 0; } else { Nula = true;
                 * //System.out.println("--> Error Minimo: " +
                 * String.valueOf(NR.getMinError())); }
                 */
            } catch (UtilsException ex) {
                if (ex.getMessage().equalsIgnoreCase("ERROR: Matriz Singular")) {
                    if (ex.getErrorCode() == Matrix.SINGULAR_MATRIX) {
                        // No se toma en cuenta y sigue con el siguiente
                        //i = Trianguladores.length;
                        if (ciclos < 50) {
                            i--;
                            ciclos++;
                        }
                        Varianza += 0.01;
                        //System.out.println("----> " + ex.getMessage());
                        Nula = true;
                    }
                }
            }
        }
        Objetivos[0] = this.LeastSquares(Objetivos, NumObjetivos);
        if (Objetivos[0] != null) {
            // Convierte la Latitud y la Longitud de lineal a angular 
            TLongitud = Objetivos[0].getLongitude();
            TLatitud = Objetivos[0].getLatitude();
            TAltura = Objetivos[0].getHeight();
            TLongitud = Trianguladores[0].Lineal2AngLongitudeDiff(TLongitud);
            TLatitud = Trianguladores[0].Lineal2AngLatitudeDiff(TLatitud);
            // Convierte las diferencias referidas al Triangulador pibote en valores absolutos
            TLongitud += Trianguladores[0].getLongitude();
            TLatitud += Trianguladores[0].getLatitude();
            TAltura += Trianguladores[0].getHeight();
        } else {
            TLongitud = -1000;
            TLatitud = -1000;
            TAltura = MinError;
        }
        return new Coordinates(TLongitud, TLatitud, TAltura);
    }

    /**
     * Realiza los calculos para la localizacion de emisor basado en la informacion
     * de los trianguladores.<br/><br/>
     * 
     * Se requieren un minimo de 4 trianguladores para poder hacer una ubicacion en 2D<br/><br/>
     * 
     * NOTA: si la medicion no es valida retorna una coordenadas -1000 para la Latitud y la Longitud<br/>
     *      
     * @return Coordenadas del objetivo ubicado
     * @throws UtilsException
     */
    public Coordinates Locate() throws UtilsException {
        Coordinates Ubicado;
        int Tamano;

        Ubicado = null;
        Tamano = Trianguladores.length;
        if (Tamano > 3) {
            if (Tamano == 4) {
                //Si se disponen de 4 trianguladores se realiza un calculo 2D
                Ubicado = this.Locate2D();
            } else {
                // Si se disponen de 5 o mas trianguladores se realiza un calculo 3D
                Ubicado = this.Locate3D();
            }
        } else {
            throw new UtilsException("No Enough Trianguladores", TDOA.NoEnoughTrianguladores);
        }
        return Ubicado;
    }
}
