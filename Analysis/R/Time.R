require(ARTool)
require(car)
require(emmeans)
library(effectsize)
library(lme4)

options(contrasts=c("contr.sum","contr.poly"))

# Import data -- check if this needs to be regenerated first
data <- read.csv("/Users/kieranwaugh/Projects/Download/Untitled/Analysis/all_data.tsv", sep="\t")

data$P <- factor(data$P)
data$Condition <- factor(data$Condition)
data$Task <- factor(data$Task)


# Aggregate across the repeated measures
data <- aggregate(subset(data, select=c("P", "Condition", "Task", "Success", "Time", "Time.to.Target", "AbsErrorDistance")), list(P = data$P, Condition=data$Condition, Task=data$Task, Success = data$Success), mean)

data <- subset(data, select=c(1, 2, 3,4,9,10,11))
as.logical(as.integer(data$Success))
print(data)
# -----------------------------------------------------------------------------
# ANOVA: Task Time
time.art <- art(Time ~ Condition * Task + (1 | P), data=data)
time.aov <- anova(time.art)
time.aov$part.eta.sq = with(time.aov, `F` * `Df` / (`F` * `Df` + `Df.res`))
cat("ANOVA: Task Time\n\n")
print(time.aov)

# Post-hoc comparisons of estimated marginal means: interaction technique
time.cond.model <- artlm(time.art, "Condition")
time.cond.posthocs <- emmeans(time.cond.model, pairwise ~ Condition)
cat("\n\nPost hoc comparisons of interaction technique\n\n")
print(time.cond.posthocs)

# Post-hoc comparisons of estimated marginal means: task type
time.task.model <- artlm(time.art, "Task")
time.task.posthocs <- emmeans(time.task.model, pairwise ~ Task)
cat("\n\nPost hoc comparisons of task type\n\n")
print(time.task.posthocs)

print(eta_squared(time.cond.model, partial = TRUE))
print(eta_squared(time.task.model, partial = FALSE))

# -----------------------------------------------------------------------------
# ANOVA: Time to Target
timeto.art <- art(Time.to.Target ~ Condition * Task + (1 | P), data=data)
timeto.aov <- anova(timeto.art)
cat("\n\nANOVA: Time to Target\n\n")
print(timeto.aov)

# Post-hoc comparisons of estimated marginal means: interaction technique
timeto.cond.model <- artlm(timeto.art, "Condition")
timeto.cond.posthocs <- emmeans(timeto.cond.model, pairwise ~ Condition)
cat("\n\nPost hoc comparisons of interaction technique\n\n")
print(timeto.cond.posthocs)

# Post-hoc comparisons of estimated marginal means: task type
timeto.task.model <- artlm(timeto.art, "Task")
timeto.task.posthocs <- emmeans(timeto.task.model, pairwise ~ Task)
cat("\n\nPost hoc comparisons of task type\n\n")
print(timeto.task.posthocs)

# -----------------------------------------------------------------------------
# ANOVA: Error distance
distance.art <- art(AbsErrorDistance ~ Condition * Task + (1 | P), data=data)
distance.aov <- anova(distance.art)
cat("\n\nANOVA: Error Distance (pixels)\n\n")
distance.aov$part.eta.sq = with(distance.aov, `F` * `Df` / (`F` * `Df` + `Df.res`))
print(distance.aov)


# Post-hoc comparisons of estimated marginal means: interaction technique
errordistance.cond.model <- artlm(distance.art, "Condition")
errordistance.cond.posthocs <- emmeans(errordistance.cond.model, pairwise ~ Condition)
cat("\n\nPost hoc comparisons of interaction technique\n\n")
print(errordistance.cond.posthocs)

# Post-hoc comparisons of estimated marginal means: task type
errordistance.task.model <- artlm(distance.art, "Task")
errordistance.task.posthocs <- emmeans(errordistance.task.model, pairwise ~ Task)
cat("\n\nPost hoc comparisons of task type\n\n")
print(errordistance.task.posthocs)
# -----------------------------------------------------------------------------
print("Success")
time2.model <- aov(Time ~ Condition * Task, data=data)
print(summary(time2.model))
print(eta_squared(time2.model, partial = FALSE))

