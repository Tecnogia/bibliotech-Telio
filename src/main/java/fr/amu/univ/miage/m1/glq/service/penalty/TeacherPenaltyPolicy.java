package fr.amu.univ.miage.m1.glq.service.penalty;

public class TeacherPenaltyPolicy implements PenaltyPolicy {

  @Override
  public double calculatePenalty(int daysOverdue) {
    if (daysOverdue <= 0) {
      return 0;
    }

    double rate;
    double maxPenalty;
    rate = 0.0; // Pas de pénalité pour les enseignants
    maxPenalty = 0.0;
    double penalty = daysOverdue * rate;
    return Math.min(penalty, maxPenalty);
  }
}
