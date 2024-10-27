package controllers;

import engine.Car;
import engine.Game;
import engine.GameObject;
import engine.Obstacle;
import engine.RotatedRectangle;

public class WallAvoidanceSeekController_copy extends Controller {
    private GameObject target;

    public WallAvoidanceSeekController_copy(GameObject target) {
        this.target = target;
    }

    @Override
    public void update(Car subject, Game game, double delta_t, double[] controlVariables) {
        // Inicializar variáveis de controle
        controlVariables[VARIABLE_STEERING] = 0;
        controlVariables[VARIABLE_THROTTLE] = 0;
        controlVariables[VARIABLE_BRAKE] = 0;

        // Calcular a direção para o alvo
        double dx = target.getX() - subject.getX();
        double dy = target.getY() - subject.getY();

        // Calcular o ângulo desejado em relação ao alvo
        double desiredAngle = Math.atan2(dy, dx);

        // Calcular a diferença de ângulo entre o carro e o alvo
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

        // Acelerar em direção ao alvo
        controlVariables[VARIABLE_THROTTLE] = 1; // Aceleração constante

        // Verificação de colisão com obstáculos
        if (isCollidingWithObstacles(subject, game)) {
            // Implementa o algoritmo de "flee" se uma colisão for detectada
            fleeFromObstacles(subject, controlVariables, game);
        }
    }

    private boolean isCollidingWithObstacles(Car subject, Game game) {
        // Defina uma distância de antecipação para detectar colisões
        double anticipationDistance = 100; // ajuste conforme necessário

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

    private void fleeFromObstacles(Car subject, double[] controlVariables, Game game) {
        // O vetor de fuga será na direção oposta ao obstáculo
        double avoidanceDx = 0;
        double avoidanceDy = 0;

        // Calcular vetor de fuga baseado nos obstáculos
        for (GameObject obstacle : game.m_objects) {
            if (obstacle instanceof Obstacle) {
                double obstacleX = obstacle.getX();
                double obstacleY = obstacle.getY();
                
                // Calcule a distância até o obstáculo
                double dx = subject.getX() - obstacleX;
                double dy = subject.getY() - obstacleY;
                double distance = Math.sqrt(dx * dx + dy * dy);

                // Se o obstáculo estiver próximo, adicione ao vetor de fuga
                if (distance < 100) { // ajuste conforme necessário
                    avoidanceDx += dx / distance; // Normaliza o vetor
                    avoidanceDy += dy / distance; // Normaliza o vetor
                }
            }
        }

        // Normaliza o vetor de fuga
        double fleeDistance = Math.sqrt(avoidanceDx * avoidanceDx + avoidanceDy * avoidanceDy);
        if (fleeDistance > 0) {
            avoidanceDx /= fleeDistance;
            avoidanceDy /= fleeDistance;

            // Ajusta a direção do volante para se desviar do obstáculo
            controlVariables[VARIABLE_STEERING] = avoidanceDx > 0 ? -1 : 1; // Gira para longe do obstáculo
            controlVariables[VARIABLE_THROTTLE] = 0.3; // Pare de acelerar ao desviar
        }
    }
}
