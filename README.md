# Flink_ImagePredictor_TensorFlow
Detector de imagenes en tiempo real utilizando un modelo de TensorFlow importado desde ApacheFlink

Ejecutar la clase DetectorImages. Recibe dos argumentos:

- El directorio donde esté la información necesaria del modelo: El modelo TF  como tal (en extensión .pb) y, opcionalmente, información que necesite para llevar acabo las predicciones (por ejemplo un fichero que contenga las etiquetas) 

- El directorio donde están las imagenes a analizar. Las que ya hubiera, o fueran llegando (streaming)

El programa debe proporcionar una salida del estilo:

New image detected. Analyzing...
The model has doubts. These are his predictions:
	water bottle: 49.54%
	nipple: 40.67%
	pop bottle: 4.86%

New image detected. Analyzing...
The model has doubts. These are his predictions:
	neck brace: 44.58%
	wig: 11.51%
	sunglasses: 7.73%
	
New image detected. Analyzing...
The model predicts with 99.46% accuracy that it is a lion
