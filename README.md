Project: I.V.I (Intelligent Vehicle Inspection)

https://www.instagram.com/ivinteligente/?utm_source=ig_web_button_share_sheet&igshid=OGQ5ZDc2ODk2ZA==

The IVI Project, or Intelligent Vehicle Inspection, was an innovative solution that utilized advanced technologies such as computer vision, neural networks, and transfer learning (in other words, artificial intelligence) to identify damages in vehicles through a mobile phone camera. I take particular pride in this project because it was a proof of concept and an unpaid project, and I was responsible for the entire development, from the machine learning model's intelligence to the front-end, in the Android application that enabled on-site use of the model. This solution offered efficiency, agility, and convenience in vehicle inspection processes, along with additional features such as secure storage of vehicle history, a list of identified damages, and license plate recognition. IVI is comparable to human analytical capacity and achieves an accuracy of 93% in damage identification and 75% in damage type classification.

IVI Features:

- Efficiency, agility, and convenience in vehicle inspection.
- Secure storage of vehicle history and images.
- List of identified and recorded damages.
- Precise GPS location.
- License plate recognition.
- Mobile application for easy access and use.

IVI arose from the need of vehicle rental companies and automakers for a faster, more objective, and fraud-proof inspection process. We conducted a proof of concept with Localiza Rent Cars, and the solution met the need for non-personal verification, eliminating conflicts between inspectors and customers, as well as the possibility of fraud. The target audience includes companies in the automotive sector looking to improve efficiency and security in their inspection operations.

I used convolutional neural networks (CNNs) as a technological solution for recognizing damages in vehicles. CNNs are highly efficient in computer vision tasks such as image classification and object detection. In addition to damage detection, IVI incorporated OCR technology to extract information from the license plates of inspected vehicles, facilitating inspection registration and recording.

IVI is developed in Python using the Keras Tensorflow library to create the CNN model. For options where the model is called via API, the Flask library is used, running on an AWS EC2 machine (a "lighter" model was also created to be embedded in the application, allowing it to function even without data network access). Data is securely stored in Firebase, with image storage in Firebase Storage and data storage in Firestore Database. The application's interface is developed in Kotlin and Java using the Android Studio framework.

The inspection process involves training the model with pre-classified images obtained from photographs taken in vehicle rental company lots and from the internet. After training, the model was integrated into the Android application, which provides users with camera and GPS resources. During inspection, the application uses the camera to capture images of the vehicle, identifying damages and classifying them by type and severity. Data is sent for storage in Firebase for subsequent analysis and model retraining.

We achieved an application with a highly efficient CNN model for identifying vehicle damages, the availability of a mobile application (IVI) that enables users to perform vehicle inspections autonomously and objectively. Integration of OCR technology for vehicle license plate recognition. Implementation of secure image and data storage using the flexible and reliable Firebase framework.

This led to the provision of an effective solution for vehicle rental companies and automakers, reducing human effort, assessment time, and the possibility of fraud. It contributed to the automation of vehicle inspection processes, improving efficiency and security in operations related to the automotive sector.

The IVI Project represents a significant advancement in vehicle inspection, harnessing the power of computer vision and neural networks to provide an intelligent and effective solution. With an accuracy of 93% (only in the proof of concept), IVI demonstrated the capacity for innovation and the practical application of artificial intelligence in a real-world scenario.
