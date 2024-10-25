package controllers;

import engine.Car;
import engine.Game;
import engine.GameObject;

public class ArriveController extends Controller{
    private GameObject target;
    private double target_radius;
    private double desaceleration_radius;

    
    public ArriveController(GameObject target, double _radius_desaceleration ,double _target_radius) {
        this.target = target;
        this.desaceleration_radius = _radius_desaceleration;
        this.target_radius = _target_radius;
    }



    @Override
    public void update(Car subject, Game game, double delta_t, double[] controlVariables) {
         // Inicializar variáveis de controle
         controlVariables[VARIABLE_STEERING] = 0;
         controlVariables[VARIABLE_THROTTLE] = 0;
         controlVariables[VARIABLE_BRAKE] = 0;
         
         // Calcular a direção para o alvo (car1)
         double dx = target.getX() - subject.getX();
         double dy = target.getY() - subject.getY();
         double distanceToTarget = Math.sqrt(dx * dx + dy * dy);
 
         // Calcular o ângulo desejado em relação ao alvo
         double desiredAngle = Math.atan2(dy, dx);
 
         // Calcular a diferença de ângulo entre o carro perseguidor e o alvo
         double angleDifference = desiredAngle - subject.getAngle();

        
 
         // Normalizar o ângulo para evitar mudanças bruscas
         if (angleDifference > Math.PI) angleDifference -= 2 * Math.PI;
         if (angleDifference < -Math.PI) angleDifference += 2 * Math.PI;
 
         // Ajustar a direção do volante para alinhar com o alvo
         if (angleDifference > 0.1) {
             controlVariables[VARIABLE_STEERING] = 1; // Virar à direita
         } else if (angleDifference < -0.1) {
             controlVariables[VARIABLE_STEERING] = -1; // Virar à esquerda
         } else {
             controlVariables[VARIABLE_STEERING] = 0; // Alinhado
         }


        

        if ( distanceToTarget < target_radius) {
            controlVariables[VARIABLE_BRAKE] = 0.5;
        }

        else if (distanceToTarget > desaceleration_radius) {
            controlVariables[VARIABLE_THROTTLE] = 1;
        }

        else{
            // Acelerar em direção ao alvo
            controlVariables[VARIABLE_THROTTLE] = 0; 
            controlVariables[VARIABLE_BRAKE] = 0;

        }

  

    }

}
