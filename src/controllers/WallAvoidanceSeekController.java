package controllers;

import engine.Car;
import engine.Game;
import engine.GameObject;
import engine.Obstacle;
import engine.RotatedRectangle;

public class WallAvoidanceSeekController extends Controller {
    private GameObject target;

    //distância de antecipação para detectar colisões
    private double anticipationDistance = 100; 

    public WallAvoidanceSeekController(GameObject target) {
        this.target = target;
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

        

        //verificar obstaculos afrente


        if (isCollidingWithObstacles(subject, game)){

            controlVariables[VARIABLE_THROTTLE] = 0;

            if (subject.getSpeed() >= 0.5)
                controlVariables[VARIABLE_BRAKE] = 0.3; 
        
        }
        else {
            controlVariables[VARIABLE_THROTTLE] = 1; 
        }


    }

    private boolean isCollidingWithObstacles(Car subject, Game game) {
        //distância de antecipação para detectar colisões

        // Posição futura do carro para prever colisão
        double futureX = subject.getX() + Math.cos(subject.getAngle()) * anticipationDistance;
        double futureY = subject.getY() + Math.sin(subject.getAngle()) * anticipationDistance;

        // Verifica colisão com cada objeto do jogo
        for (GameObject obstacle : game.m_objects) {
            if (obstacle instanceof Obstacle) { // Verifique se o objeto é um obstáculo
                if (RotatedRectangle.RotRectsCollision(
                        new RotatedRectangle(futureX, futureY, subject.getCollisionBox().S.x, subject.getCollisionBox().S.y, subject.getAngle()),
                        obstacle.getCollisionBox())) {
                    return true; // Colisão detectada
                }
            }
        }
        return false; // Sem colisão
    }

    

}
