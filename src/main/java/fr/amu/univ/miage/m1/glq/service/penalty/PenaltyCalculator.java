package fr.amu.univ.miage.m1.glq.service.penalty;

import fr.amu.univ.miage.m1.glq.model.Member;

public class PenaltyCalculator {
  // Configuration en dur
  static final double PENALTY_RATE_PER_DAY = 0.50;

  private final PenaltyPolicyFactory penaltyPolicyFactory = new PenaltyPolicyFactory();

  public PenaltyCalculator() {}

  /** Calcule la pénalité de retard. */
  public double calculatePenalty(Member member, int daysOverdue) {

    return penaltyPolicyFactory.getPolicy(member.getMemberType()).calculatePenalty(daysOverdue);
  }
}
