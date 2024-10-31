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
        controlVariables[VARIABLE_STEERING] = 0;
        controlVariables[VARIABLE_THROTTLE] = 0;
        controlVariables[VARIABLE_BRAKE] = 0;
        
        // Calcular direção e distância ao alvo
        double dx = target.getX() - subject.getX();
        double dy = target.getY() - subject.getY();
        double distanceToTarget = Math.sqrt(dx * dx + dy * dy);
        
        // Calcular ângulo desejado e diferença de ângulo
        double desiredAngle = Math.atan2(dy, dx);
        double angleDifference = desiredAngle - subject.getAngle();
        
        if (angleDifference > Math.PI) angleDifference -= 2 * Math.PI;
        if (angleDifference < -Math.PI) angleDifference += 2 * Math.PI;
    
        if (angleDifference > 0.1) {
            controlVariables[VARIABLE_STEERING] = 1;
        } else if (angleDifference < -0.1) {
            controlVariables[VARIABLE_STEERING] = -1;
        } else {
            controlVariables[VARIABLE_STEERING] = 0;
        }
        
        // Se estiver dentro do target_radius, parar
        if (distanceToTarget < target_radius) {
            controlVariables[VARIABLE_THROTTLE] = 0;
            
            //System.out.println(subject.getSpeed());
            if (subject.getSpeed() > 8){
                controlVariables[VARIABLE_BRAKE] = 1;  // Parada total
            } else {
                controlVariables[VARIABLE_BRAKE] = 0;  
            }
        } else {
            if (distanceToTarget > desaceleration_radius){
                controlVariables[VARIABLE_THROTTLE] = 1;
            }else
            {
                // Determinar velocidade alvo baseada na distância
                double maxSpeed = 1.0; // Defina a velocidade máxima apropriada
                double targetSpeed;
                
            
                targetSpeed = maxSpeed * (distanceToTarget / desaceleration_radius);
                
        
                // Calcular a aceleração necessária
                double currentSpeed = subject.getSpeed();
                double acceleration = (targetSpeed - currentSpeed) / delta_t;
                double maxAcceleration = 0.5; // Limite máximo de aceleração
        
                if (Math.abs(acceleration) > maxAcceleration) {
                    acceleration = Math.signum(acceleration) * maxAcceleration;
                }
        
                // Aplicar aceleração calculada aos controles
                controlVariables[VARIABLE_THROTTLE] = Math.max(0, acceleration / maxAcceleration);
                controlVariables[VARIABLE_BRAKE] = acceleration < 0 ? -acceleration / maxAcceleration : 0;
            }
        }
    }
    

}
