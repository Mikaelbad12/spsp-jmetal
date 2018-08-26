//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.
//

package net.rodrigoamaral.jmetal.util.evaluator.impl;

import net.rodrigoamaral.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;

import java.util.List;

/**
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class SequentialSolutionListEvaluator<S extends Solution<?>> implements SolutionListEvaluator<S> {

  @SuppressWarnings({ "unchecked", "rawtypes" })
@Override
  public List<S> evaluate(List<S> solutionList, Problem<S> problem) throws JMetalException {
      if (problem instanceof ConstrainedProblem) {
        solutionList.stream().forEach(s -> {
          problem.evaluate(s);
          ((ConstrainedProblem) problem).evaluateConstraints(s);
        });
      } else {
        solutionList.stream().forEach(s -> problem.evaluate(s));
      }

    return solutionList;
  }

  @Override public void shutdown() {
    ;
  }
}
