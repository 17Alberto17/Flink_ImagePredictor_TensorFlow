# Flink_ImagePredictor_TensorFlow
Detector de imagenes en tiempo real utilizando un modelo de TensorFlow importado desde ApacheFlink

Ejecutar la clase DetectorImages. Recibe dos argumentos:

- El directorio donde esté la información necesaria del modelo: El modelo TF  como tal (en extensión .pb) y, opcionalmente, información que necesite para llevar acabo las predicciones (por ejemplo un fichero que contenga las etiquetas) 

- El directorio donde están las imagenes a analizar. Las que ya hubiera, o fueran llegando (streaming)

El programa debe proporcionar, por cada imagen recibida, una salida indicado su predicción y correspondiente grado de confianza. Si es inferior al 75% ofrecerá 3 alternativas
