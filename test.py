import numpy as np
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d.art3d import Poly3DCollection
# We import the numpy library to easily perform matrix operations. The reason we imported matplotlib.pyplot and Poly3DCollection is to see the process visually.
#Homogeneous point we want to convert
point_3d = np.array([0, 0, -10, 1])
#Type of the projection we want
projection_type = "orthographic"
#Coordinates of the view volume
left = -3
right = 3
bottom = -3
top = 3
near = -1
far = 1
# At this stage, we take any point from the world coordinate system that we want to transform. We keep the type of projection we want to implement in a variable. You can randomly select the coordinates of the view volume. Choosing different view volumes will change the properties of the image you will get on the screen. To ensure your chosen point appears on the screen, ensure it falls within the boundaries of the view volume.
#Creating camera matrix
rotation_matrix = np.array([
[1, 0, 0, 0],
[0, 1, 0, 0],
[0, 0, 1, 0],
[0, 0, 0, 1]
])
translation_matrix = np.array([
[1, 0, 0, 0],
[0, 1, 0, 0],
[0, 0, 1, 0],
[0, 0, 0, 1]
])
camera_matrix = rotation_matrix @ translation_matrix
#Camera is at the origin of world coordinate system, looking towards -z axis
# For the sake of simplicity, we assume that there is no need for translation and rotation operations, which means the world coordinate system is already aligned with the camera coordinate system.
#Projection Matrix
def orthographic_projection(left, right, bottom, top, near, far):
  op_matrix = np.array([
  [2 / (right - left), 0, 0, -(right + left) / (right - left)],
  [0, 2 / (top - bottom), 0, -(top + bottom) / (top - bottom)],
  [0, 0, -2 / (far - near), -(far + near) / (far - near)],
  [0, 0, 0, 1]
  ])
  return op_matrix
def perspective_projection(left, right, bottom, top, near, far):
  pp_matrix = np.array([
  [(2 * near) / (right - left), 0, (right + left) / (right - left), 0],
  [0, (2 * near) / (top - bottom), (top + bottom) / (top - bottom), 0],
  [0, 0, -(far + near) / (far - near), -(2 * far * near) / (far - near)],
  [0, 0, -1, 0]
  ])
  return pp_matrix
# We are creating two different functions for the two different projection types we will perform. Each function takes the necessary parameters for the view volume and turns it into projection matrices.
#ViewPort Matrix
nx = 600
ny = 600
viewport_matrix = np.array([
[nx / 2, 0, 0, (nx - 1) / 2],
[0, ny / 2, 0, (ny - 1) / 2],
[0, 0, 0.5, 0.5],
])
# The value “nx” corresponds to the desired screen width, while “ny” represents the screen"s height. We generate the viewport matrix according to the explanation provided previously.
#Choosing projection matrix associated with projection type
if(projection_type == "orthographic"):
  projection_matrix = orthographic_projection(left, right, bottom, top, near, far)
elif (projection_type == "perspective"):
  projection_matrix = perspective_projection(left, right, bottom, top, near, far)
#Applying the matrices in the described order.
point_after_CM = camera_matrix @ point_3d
point_after_PM = projection_matrix @ point_after_CM
#Normalization of the projected point
point_after_PM /= point_after_PM[3]
point_after_VP = viewport_matrix @ point_after_PM
# We obtain the projection matrix according to the projection type we have chosen. Then we apply the projection matrices to the 3D point in the order we specified. After projection, we normalize the homogeneous coordinate. In this way, we are proportioning the points according to the depth in perspective projection. Notice that this operation has no effect on orthographic projection. This is because the weight of point_after_PM is already equal to 1.
# Great job! We"ve successfully determined how a 3D point gets projected onto a 2D screen. Now, let"s create a code that visually demonstrates this process. To do this, we"ll choose the corner points of a cube in the 3D world, and then figure out where these points will appear on the 2D screen after projection.
cube_vertices = np.array([
[-1, -1, -1, 1], # Vertex 0
[1, -1, -1, 1], # Vertex 1
[1, 1, -1, 1], # Vertex 2
[-1, 1, -1, 1], # Vertex 3
[-1, -1, 1, 1], # Vertex 4
[1, -1, 1, 1], # Vertex 5
[1, 1, 1, 1], # Vertex 6
[-1, 1, 1, 1] # Vertex 7
])
# Translate cube vertices to center at (0, 0, -10)
# print(cube_vertices)
translation_vector = np.array([0, 0, -10, 0])
cube_vertices = cube_vertices + translation_vector
cube_edges = [
[0, 1], [1, 2], [2, 3], [3, 0],
[4, 5], [5, 6], [6, 7], [7, 4],
[0, 4], [1, 5], [2, 6], [3, 7]
]
# In the provided code, our initial step was to generate a cube. We positioned its center at the origin and made each side 2 units long. Afterward, we shifted the cube"s center to the point (0, 0, -10) to ensure it remains within our designated view volume. Regarding the “cube_edges” explanation, it involves linking the indices of specific vertices that we intend to form edges between.
# Create a figure and 3D subplot
fig = plt.figure(figsize=(10, 6))
ax3d = fig.add_subplot(121, projection="3d")
ax2d = fig.add_subplot(122)
# Plot the cube in 3D
for edge in cube_edges:
  ax3d.plot(cube_vertices[edge, 0], cube_vertices[edge, 1], cube_vertices[edge, 2], color="blue")
# Transformed cube vertices after camera and projection matrices
cube_after_CM = camera_matrix @ cube_vertices.T
cube_after_PM = projection_matrix @ cube_after_CM
cube_after_PM /= cube_after_PM[3]
cube_after_VP = viewport_matrix @ cube_after_PM
print(cube_after_VP)
# Plot the projected cube in 2D
for edge in cube_edges:
  start_idx, end_idx = edge
  start_point = cube_after_VP[:2, start_idx]
  end_point = cube_after_VP[:2, end_idx]
  ax2d.plot([start_point[0], end_point[0]], [start_point[1], end_point[1]], color="red")
# Set labels and title
ax3d.set_xlabel("X")
ax3d.set_ylabel("Y")
ax3d.set_zlabel("Z")
ax3d.set_title("3D Cube Projection")
ax2d.set_xlabel("X")
ax2d.set_ylabel("Y")
ax2d.set_xlim(0, nx)
ax2d.set_ylim(0, ny)
ax2d.set_title("2D Projection on Screen")
plt.tight_layout()
plt.show()