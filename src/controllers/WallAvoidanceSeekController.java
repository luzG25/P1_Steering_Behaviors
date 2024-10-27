package controllers;

import engine.Car;
import engine.Game;
import engine.GameObject;
import engine.Obstacle;
import engine.RotatedRectangle;

public class WallAvoidanceSeekController extends Controller {
    private GameObject target;
    

    //distância de antecipação para detectar colisões
    private double anticipationDistance = 75; 

    public WallAvoidanceSeekController(GameObject target) {
        this.target = target;
    }

    @Override
    public void update(Car subject, Game game, double delta_t, double[] controlVariables) {
        // Inicializar variáveis de controle
        controlVariables[VARIABLE_STEERING] = 0;
        controlVariables[VARIABLE_THROTTLE] = 0;
        controlVariables[VARIABLE_BRAKE] = 0;

        //verificar obstaculos afrente
        if (isCollidingWithObstacles(subject, game, subject.getAngle())){

            controlVariables[VARIABLE_THROTTLE] = 0;

            if (subject.getSpeed() >= 0.5)
                controlVariables[VARIABLE_BRAKE] = 0.2; 
                flee(subject, game, controlVariables);
        
        }
        else {
            seek(subject, controlVariables, game);
            controlVariables[VARIABLE_THROTTLE] = 1; 
        }
        


    }

    private void seek(Car subject, double[] controlVariables, Game game){
        System.out.println("Seeking");

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
         if (angleDifference > 0.1 && !(isCollidingWithObstacles(subject, game, subject.getAngle() + (Math.PI / 2)))) {
             controlVariables[VARIABLE_STEERING] = 1; // Virar à direita
         } else if (angleDifference < -0.1 && !(isCollidingWithObstacles(subject, game, subject.getAngle() - (Math.PI / 2)))) {
             controlVariables[VARIABLE_STEERING] = -1; // Virar à esquerda
         } else {
             controlVariables[VARIABLE_STEERING] = 0; // Alinhado
         }
    }

    private void flee(Car subject ,  Game game,double[] controlVariables){
        System.out.println("Flee");

        //obter obstaculo
        GameObject obstac = getCollidingObstacle(subject, game);

        // Calcular a direção para o alvo (car1)
        double dx = obstac.getX() - subject.getX();
        double dy = obstac.getY() - subject.getY();

        // Calcular o ângulo desejado em relação ao alvo
        double desiredAngle = Math.atan2(dy, dx);

        // Calcular a diferença de ângulo entre o carro perseguidor e o alvo
        double angleDifference = desiredAngle - subject.getAngle();

        // Normalizar o ângulo para evitar mudanças bruscas
        if (angleDifference > Math.PI) angleDifference -= 2 * Math.PI;
        if (angleDifference < -Math.PI) angleDifference += 2 * Math.PI;

        // Ajustar a direção do volante para alinhar com o alvo
        if (angleDifference > 0.1) {
            controlVariables[VARIABLE_STEERING] = -1; 
        } else if (angleDifference < -0.1) {
            controlVariables[VARIABLE_STEERING] = 1; // Virar à esquerda
        } else {
            controlVariables[VARIABLE_STEERING] = 0; // Alinhado
        }

        controlVariables[VARIABLE_THROTTLE] = 0.05; 


   }

    private boolean isCollidingWithObstacles(Car subject, Game game, double angle) {
        //distância de antecipação para detectar colisões

        // Posição futura do carro para prever colisão
        double futureX = subject.getX() + Math.cos(angle) * anticipationDistance;
        double futureY = subject.getY() + Math.sin(angle) * anticipationDistance;

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


    private GameObject getCollidingObstacle(Car subject, Game game) {
        // Retorna o obstáculo que está colidindo com o carro
        double futureX = subject.getX() + Math.cos(subject.getAngle()) * anticipationDistance;
        double futureY = subject.getY() + Math.sin(subject.getAngle()) * anticipationDistance;

        for (GameObject obstacle : game.m_objects) {
            if (obstacle instanceof Obstacle) {
                if (RotatedRectangle.RotRectsCollision(
                        new RotatedRectangle(futureX, futureY, subject.getCollisionBox().S.x, subject.getCollisionBox().S.y, subject.getAngle()),
                        obstacle.getCollisionBox())) {
                    return obstacle; // Retorna o obstáculo colidido
                }
            }
        }
        return null; // Sem colisão
    }

}
