//Multiplicación de matrices distribuida
//Realizó: Carlos Armando Rojas de la Rosa
//Motivó: Mary
//Fecha: 12/oct/2020
//Materia: Desarrollo de sistemas distribuidos
//Grupo: 4CV2

import java.net.Socket;
import java.net.ServerSocket;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.lang.Thread;
import java.nio.ByteBuffer;

class Matriz{
	static Object lock = new Object();
	static int N = 1000;
	static int[][] A = new int[N][N];
	static int[][] B = new int[N][N];
	
    static int[][] C1 = new int[N/2][N/2];
    static int[][] C2 = new int[N/2][N/2];
    static int[][] C3 = new int[N/2][N/2];
    static int[][] C4 = new int[N/2][N/2];
    static int[][] C = new int[N][N];
    static int contador = 0;

	static void read(DataInputStream f,byte[] b, int posicion, int longitud) throws Exception{

		while( longitud > 0 ){
			int n = f.read(b,posicion,longitud);
			posicion += n;
			longitud -= n;
		}
	}

	static class Worker extends Thread{

		Socket conexion;
		Worker( Socket conexion ){
			this.conexion = conexion;
		}

		public void run(){

			try{

				DataOutputStream salida = new DataOutputStream( conexion.getOutputStream() );
				DataInputStream entrada = new DataInputStream( conexion.getInputStream() );
				
                //Aqui se debe poner el código del servidor
	
                //Paso 1. Inicializamos las Matrices A y B
				for(int i = 0; i < N; i++){
                    				
                	for(int j = 0; j < N; j++){
						A[i][j] = 2 * i + j;						
                        B[i][j] = 2 * i - j;
					}
                 }

				//Transponemos la matriz B
                
				for(int i = 0; i < N; i++)
					for(int j = 0; j < i; j++){
					int x = B[i][j];
					B[i][j] = B[j][i];
					B[j][i] = x;
                    
					}

                int nodoConectado = entrada.readInt();
                System.out.println("\nNodo "+nodoConectado+" Conectado");    
                
                if(nodoConectado == 1){
                    contador++;
				//Paso 2. Enviar la matriz A1 al nodo 1
				int num = 0;
				for(int i = 0; i < N/2; i++)
					for(int j = 0; j < N; j++){
						num = A[i][j];
						salida.writeInt( num );
					}
				//paso 3. Enviar la matriz B1 al nodo 1
				for(int i = 0; i < N/2; i++)
					for(int j = 0; j < N; j++){
						num = B[i][j];
						salida.writeInt( num );
					}

                

                //Recibe C1 del nodo 1        
                
                for(int i = 0; i < N/2; i++){
                    for(int j = 0; j < N/2; j++){
                        int valor = entrada.readInt();
                        C1[i][j] = valor;
                    }
                }
                System.out.println("Procesamiento de "+nodoConectado+" listo");
                }

                if( nodoConectado == 2 ){
                contador++;
                int num;

                //paso 4. Enviar la matriz A1 al nodo 2
				for(int i = 0; i < N/2; i++)
					for(int j = 0; j < N; j++){
						num = A[i][j];
						salida.writeInt( num );
					}
                //Paso 5. Enviar B2 al nodo 2             
                for(int i = N/2 ; i < N; i++){
                    for(int j = 0; j < N; j++){
                        num = B[i][j];
                        salida.writeInt( num );
                    }
                }

                //Recibe C2 del nodo 2           
                for(int i = 0; i < N/2; i++){
                    for(int j = 0; j < N/2; j++){
                        int valor = entrada.readInt();
                        C2[i][j] = valor;
                    }
                }
                System.out.println("Procesamiento del nodo "+nodoConectado+" listo");  
                }

                if( nodoConectado == 3 ){
                contador++;
                int num;

                //paso 4. Enviar la matriz A2 al nodo 3
				for(int i = N/2 ; i < N; i++)
					for(int j = 0; j < N; j++){
						num = A[i][j];
						salida.writeInt( num );
					}
                //Paso 5. Enviar B1 al nodo 3             
                for(int i = 0; i < N/2; i++){
                    for(int j = 0; j < N; j++){
                        num = B[i][j];
                        salida.writeInt( num );
                    }
                }

                //Recibe C3 del nodo 3           
                for(int i = 0; i < N/2; i++){
                    for(int j = 0; j < N/2; j++){
                        int valor = entrada.readInt();
                        C3[i][j] = valor;
                    }
                }  
                System.out.println("Procesamiento de "+nodoConectado+" listo");
                }

                if( nodoConectado == 4 ){
                contador++;
                int num;

                //paso 4. Enviar la matriz A2 al nodo 4
				for(int i = N/2; i < N; i++)
					for(int j = 0; j < N; j++){
						num = A[i][j];
						salida.writeInt( num );
					}
                //Paso 5. Enviar B2 al nodo 4             
                for(int i = N/2 ; i < N; i++){
                    for(int j = 0; j < N; j++){
                        num = B[i][j];
                        salida.writeInt( num );
                    }
                }

                //Recibe C4 del nodo 4           
                for(int i = 0; i < N/2; i++){
                    for(int j = 0; j < N/2; j++){
                        int valor = entrada.readInt();
                        C4[i][j] = valor;
                    }
                }
                System.out.println("Procesamiento de "+nodoConectado+" listo");  
                }

                //Une C1 C2 C3 y C4
                double checksum = 0;
                if(contador == 4){
                    System.out.println();
                    System.out.println("Calculando Checksum...");
                for(int i = 0; i < N/2; i++){
                    for(int j = 0; j < N/2; j++){
                        C[i][j] = C1[i][j];
                        C[i][j + N/2] = C2[i][j];
                        C[i + N/2][j] = C3[i][j];
                        C[i + N/2][j + N/2] = C4[i][j];
                    }
                }
                for(int i = 0; i < N; i++){
                    for(int j = 0; j < N; j++){
                        checksum += C[i][j];                    
                    }
                }
                System.out.println("Checksum READY!");
                
                System.out.println("Checksum:"+checksum);
                
                //Imprime C
                if(N == 4){
                    System.out.println("\nMatriz C");
                    for(int i = 0; i < N; i++){
                        System.out.println();
                        for(int j = 0; j < N; j++){
                            System.out.print(C[i][j]+",");
                        }
                    }
                }
                System.out.println();
                }
                

				entrada.close();
				salida.close();
				conexion.close();

			}catch( Exception e ){
				System.err.println( e.getMessage() );
			}
		}
	}

public static void main(String[] args) throws Exception
  {
 
    if (args.length != 1)
    {
	//Se indica si se debe comportar como cliente o servidor
      System.err.println("Uso:");
      System.err.println("java PI <nodo>");
      System.exit(0);
    }
    int nodo = Integer.valueOf(args[0]);
    if (nodo == 0)
    {
      
	System.out.println("Servidor");

	//Instanciamos un objeto de la clase ServerSocket
	ServerSocket servidor = new ServerSocket(50000);

	//Declaramos un vector w de la clase Worker con 3 elementos
	Worker arrayWorkers[] = new Worker[4];
	
	
	//Aquí se crean los clientes que van a realizar las sumas	
	for(int i = 0; i < 4; i++){

		Socket conexion;
		conexion = servidor.accept();

		Worker w = new Worker(conexion);
		arrayWorkers[i] = w;

		arrayWorkers[i].start();		
	}
   

    }
    else
    {
     
	System.out.println("Cliente "+nodo);
	Socket conexion = null;
	//Algoritmo para realizar reintento en caso de no concretar la conexión con el servidor
	for(;;)
		try{
			conexion = new Socket("localhost",50000);
			break;		
		}
		catch(Exception e){
			Thread.sleep(100);	
		}
	   //Se declaran los buffers de entrada y salida
	DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
	DataInputStream entrada = new DataInputStream(conexion.getInputStream());

	//aquí pasa la mágia de los clientes
    if( nodo == 1){
        int numero = 0;
        
        int[][] A1 = new int[N / 2][ N ];
        int[][] B1 = new int[N / 2][ N ];
        int[][] C1 = new int[ N / 2][ N / 2 ];

        salida.writeInt(nodo);
        //Recibe A1
        
        for(int i = 0; i < N/2; i++){
            for(int j = 0; j < N; j++){
                numero = entrada.readInt();        
                A1[i][j] = numero;
            }
        }
        //Recibe B1
        for(int i = 0; i < N/2; i++){
            for(int j = 0; j < N; j++){
                numero = entrada.readInt();        
                B1[i][j] = numero;
            }
        }
        
        //Multiplica A1 * B1 para Obtener C1 
            
        for(int i = 0; i < N/2; i++){
            for(int j = 0; j < N/2 ; j++){
                for(int k = 0; k < N; k++){
			        C1[i][j] += A1[i][k] * B1[j][k];	
		        }
                        
            }
        }        
            //Mandamos C1 al nodo 0
            for(int i = 0; i < N/2; i++){
                for(int j = 0; j < N/2; j++){
                        int valor = C1[i][j]; 
                        salida.writeInt(valor);               
                }
            }
               
    }
	// Código de Nodo 2
    if( nodo == 2 ){
        salida.writeInt(nodo);
        
        int numero = 0;
        
        int[][] A1 = new int[N / 2][ N ];
        int[][] B2 = new int[N / 2][ N ];
        int[][] C2 = new int[ N / 2][ N / 2 ];

        //Recibe A1
        for(int i = 0; i < N/2; i++){
            for(int j = 0; j < N; j++){
                numero = entrada.readInt();        
                A1[i][j] = numero;
            }
        }
        //Recibe B1
        for(int i = 0; i < N/2; i++){
            for(int j = 0; j < N; j++){
                numero = entrada.readInt();        
                B2[i][j] = numero;
            }
        }
        //Multiplica A1 * B2 para Obtener C2 
            
        for(int i = 0; i < N/2; i++){
            for(int j = 0; j < N/2 ; j++){
                for(int k = 0; k < N; k++){
			        C2[i][j] += A1[i][k] * B2[j][k];	
		        }
                        
            }
        }
        //Mandamos C2 al nodo 0
            for(int i = 0; i < N/2; i++){
                for(int j = 0; j < N/2; j++){
                        int valor = C2[i][j]; 
                        salida.writeInt(valor);               
                }
            }

    }

    if( nodo == 3){
        salida.writeInt(nodo);
        
        int numero = 0;
        
        int[][] A2 = new int[N / 2][ N ];
        int[][] B1 = new int[N / 2][ N ];
        int[][] C3 = new int[ N / 2][ N / 2 ];

        //Recibe A2
        for(int i = 0; i < N/2; i++){
            for(int j = 0; j < N; j++){
                numero = entrada.readInt();        
                A2[i][j] = numero;
            }
        }
        //Recibe B1
        for(int i = 0; i < N/2; i++){
            for(int j = 0; j < N; j++){
                numero = entrada.readInt();        
                B1[i][j] = numero;
            }
        }
        //Multiplica A2 * B1 para Obtener C3
            
        for(int i = 0; i < N/2; i++){
            for(int j = 0; j < N/2 ; j++){
                for(int k = 0; k < N; k++){
			        C3[i][j] += A2[i][k] * B1[j][k];	
		        }
                        
            }
        }
        //Mandamos C3 al nodo 0
            for(int i = 0; i < N/2; i++){
                for(int j = 0; j < N/2; j++){
                        int valor = C3[i][j]; 
                        salida.writeInt(valor);               
                }
            }

    }

    if( nodo == 4){
        salida.writeInt(nodo);
        
        int numero = 0;
        
        int[][] A2 = new int[N / 2][ N ];
        int[][] B2 = new int[N / 2][ N ];
        int[][] C4 = new int[ N / 2][ N / 2 ];

        //Recibe A2
        for(int i = 0; i < N/2; i++){
            for(int j = 0; j < N; j++){
                numero = entrada.readInt();        
                A2[i][j] = numero;
            }
        }
        //Recibe B2
        for(int i = 0; i < N/2; i++){
            for(int j = 0; j < N; j++){
                numero = entrada.readInt();        
                B2[i][j] = numero;
            }
        }
        //Multiplica A2 * B2 para Obtener C4
            
        for(int i = 0; i < N/2; i++){
            for(int j = 0; j < N/2 ; j++){
                for(int k = 0; k < N; k++){
			        C4[i][j] += A2[i][k] * B2[j][k];	
		        }
                        
            }
        }
        //Mandamos C4 al nodo 0
            for(int i = 0; i < N/2; i++){
                for(int j = 0; j < N/2; j++){
                        int valor = C4[i][j]; 
                        salida.writeInt(valor);               
                }
            }

    }
	salida.close();
	entrada.close();
	conexion.close();
	
    }
  }

}
