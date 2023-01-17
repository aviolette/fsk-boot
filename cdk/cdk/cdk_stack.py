from aws_cdk import (
    Stack,
)
from aws_cdk import (
    aws_ec2 as ec2,
    aws_iam as iam,
    aws_ecs as ecs,
    aws_ecs_patterns as ecs_patterns,
    aws_dynamodb as dynamodb,
)
import aws_cdk.aws_ecr as ecr

from constructs import Construct


class CdkStack(Stack):
    def __init__(self, scope: Construct, construct_id: str, **kwargs) -> None:
        super().__init__(scope, construct_id, **kwargs)

        vpc = ec2.Vpc(self, "FskApp", max_azs=3)  # default is all AZs in region

        cluster = ecs.Cluster(self, "FskCluster", vpc=vpc)

        fargate_service = ecs_patterns.ApplicationLoadBalancedFargateService(
            self,
            "FreudianSearch",
            cluster=cluster,
            cpu=512,
            desired_count=1,
            task_image_options=ecs_patterns.ApplicationLoadBalancedTaskImageOptions(
                container_port=8080,
                image=ecs.ContainerImage.from_ecr_repository(
                    ecr.Repository.from_repository_name(
                        self, "FskRepository", "fskboot"
                    )
                ),
            ),
            memory_limit_mib=2048,
            public_load_balancer=True,
        )

        table = dynamodb.Table(
            self,
            id="FreudianSearchTable",
            table_name="FreudianSearch",
            partition_key=dynamodb.Attribute(
                name="pk", type=dynamodb.AttributeType.STRING
            ),
            sort_key=dynamodb.Attribute(name="sk", type=dynamodb.AttributeType.STRING),
        )

        table.add_global_secondary_index(
            index_name="GSI1",
            partition_key=dynamodb.Attribute(
                name="gsi1pk", type=dynamodb.AttributeType.STRING
            ),
            sort_key=dynamodb.Attribute(
                name="gsi1sk", type=dynamodb.AttributeType.STRING
            ),
        )
        dynamo_db_endpoint = vpc.add_gateway_endpoint(
            "DynamoDbEndpoint", service=ec2.GatewayVpcEndpointAwsService.DYNAMODB
        )
        dynamo_db_endpoint.add_to_policy(
            iam.PolicyStatement(
                principals=[iam.AnyPrincipal()],
                actions=[
                    "dynamodb:DescribeTable",
                    "dynamodb:PutItem",
                    "dynamodb:ListTables",
                ],
                resources=["*"],
            )
        )
        table.grant_full_access(fargate_service.task_definition.task_role)
