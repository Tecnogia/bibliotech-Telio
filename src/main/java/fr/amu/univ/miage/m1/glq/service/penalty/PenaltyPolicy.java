package fr.amu.univ.miage.m1.glq.service.penalty;

public interface PenaltyPolicy {

  double calculatePenalty(int daysOverdue);
}
