package fr.amu.univ.miage.m1.glq.service.penalty;

import java.util.Map;

public class PenaltyPolicyFactory {

  Map<String, PenaltyPolicy> penaltyPolicies =
      Map.of("STUDENT", new StudentPenaltyPolicy(), "TEACHER", new TeacherPenaltyPolicy());

  public PenaltyPolicy getPolicy(String memberType) {
    return penaltyPolicies.get(memberType);
  }
}
